# 2.1 — Exporting PyTorch Models to TFLite (Python Side)

> **Module 2 · Lesson 1**  
> 📓 Notebook · ⏱️ ~35 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Export any PyTorch model to ONNX format
- Convert ONNX to TFLite using `onnx-tf` and `tf2onnx`
- Apply INT8 post-training quantization to shrink the model
- Validate the converted model gives matching outputs
- Know when each conversion path works and when it fails

---

## 0. Install Dependencies

```bash
pip install torch torchvision onnx onnx-tf tensorflow numpy pillow
```

---

## 1. The Conversion Pipeline

There is no direct PyTorch → TFLite converter. The path goes through ONNX:

```
PyTorch Model (.pth)
        │
        ▼  torch.onnx.export()
ONNX Model (.onnx)
        │
        ▼  onnx-tf or tf2onnx
TensorFlow SavedModel
        │
        ▼  TFLiteConverter
TFLite Model (.tflite)   ← deploy to Android
```

Each step can fail for different reasons — we'll handle them all.

---

## 2. Step 1 — Load or Train a PyTorch Model

```python
# ── Load a pretrained MobileNetV2 ─────────────────────
import torch
import torchvision.models as models

# Load pretrained MobileNetV2
model = models.mobilenet_v2(pretrained=True)
model.eval()  # CRITICAL: set to eval mode before export

print(f"Model parameters : {sum(p.numel() for p in model.parameters()):,}")
print(f"Model size (FP32): {sum(p.numel() * 4 for p in model.parameters()) / 1e6:.1f} MB")
```

**Expected output:**
```
Model parameters : 3,504,872
Model size (FP32): 14.0 MB
```

> ⚠️ Always call `model.eval()` before export. This switches BatchNorm and Dropout to inference mode — without this, your converted model will produce wrong results.

---

## 3. Step 2 — Export PyTorch → ONNX

```python
# ── Export to ONNX ────────────────────────────────────
import torch
import os

def export_to_onnx(model, output_path="mobilenetv2.onnx", input_size=(1, 3, 224, 224)):
    """Export a PyTorch model to ONNX format."""

    # Create dummy input matching the model's expected shape
    dummy_input = torch.randn(*input_size)

    print(f"Exporting to ONNX: {output_path}")
    torch.onnx.export(
        model,
        dummy_input,
        output_path,

        # Metadata
        export_params=True,         # store trained weights
        opset_version=12,           # ONNX opset version (12 = good TFLite compat)

        # Input/output names (used in Android inference code)
        input_names=["input"],
        output_names=["output"],

        # Dynamic axes — allows different batch sizes
        dynamic_axes={
            "input":  {0: "batch_size"},
            "output": {0: "batch_size"}
        },

        verbose=False
    )

    size_mb = os.path.getsize(output_path) / 1e6
    print(f"✅ ONNX export complete: {output_path} ({size_mb:.1f} MB)")
    return output_path

onnx_path = export_to_onnx(model)
```

---

## 4. Step 3 — Validate the ONNX Model

Always validate before converting further. Catches broken exports early.

```python
# ── Validate ONNX model ───────────────────────────────
import onnx
import onnxruntime as ort
import numpy as np

def validate_onnx(onnx_path, input_size=(1, 3, 224, 224)):
    """Check the ONNX model is valid and produces correct outputs."""

    # 1. Check model structure
    model = onnx.load(onnx_path)
    onnx.checker.check_model(model)
    print("✅ ONNX model structure is valid")

    # 2. Check inputs/outputs
    print(f"\nInputs:")
    for inp in model.graph.input:
        shape = [d.dim_value for d in inp.type.tensor_type.shape.dim]
        print(f"  {inp.name}: {shape}")

    print(f"\nOutputs:")
    for out in model.graph.output:
        shape = [d.dim_value for d in out.type.tensor_type.shape.dim]
        print(f"  {out.name}: {shape}")

    # 3. Run inference to check output values
    session = ort.InferenceSession(onnx_path)
    dummy = np.random.randn(*input_size).astype(np.float32)
    outputs = session.run(None, {"input": dummy})

    print(f"\nInference test:")
    print(f"  Output shape: {outputs[0].shape}")
    print(f"  Output range: [{outputs[0].min():.3f}, {outputs[0].max():.3f}]")
    print(f"  Top class   : {np.argmax(outputs[0])}")
    print("✅ ONNX inference successful")

validate_onnx(onnx_path)
```

---

## 5. Step 4 — Convert ONNX → TensorFlow SavedModel

```python
# ── ONNX → TensorFlow SavedModel ─────────────────────
from onnx_tf.backend import prepare
import onnx

def onnx_to_tf_savedmodel(onnx_path, output_dir="mobilenetv2_savedmodel"):
    """Convert ONNX model to TensorFlow SavedModel format."""

    print(f"Converting ONNX → TF SavedModel...")
    onnx_model = onnx.load(onnx_path)
    tf_rep = prepare(onnx_model)
    tf_rep.export_graph(output_dir)

    print(f"✅ TF SavedModel saved to: {output_dir}/")
    return output_dir

savedmodel_dir = onnx_to_tf_savedmodel(onnx_path)
```

---

## 6. Step 5 — Convert TensorFlow → TFLite

```python
# ── TF SavedModel → TFLite ────────────────────────────
import tensorflow as tf
import numpy as np

def savedmodel_to_tflite(
    savedmodel_dir,
    output_path="mobilenetv2.tflite",
    quantize=False,
    representative_dataset=None
):
    """
    Convert TF SavedModel to TFLite.

    Args:
        savedmodel_dir: path to TF SavedModel directory
        output_path: output .tflite file path
        quantize: if True, apply INT8 post-training quantization
        representative_dataset: generator for INT8 calibration (required if quantize=True)
    """

    converter = tf.lite.TFLiteConverter.from_saved_model(savedmodel_dir)

    if quantize:
        print("Applying INT8 post-training quantization...")
        converter.optimizations = [tf.lite.Optimize.DEFAULT]
        converter.target_spec.supported_ops = [
            tf.lite.OpsSet.TFLITE_BUILTINS_INT8,
            tf.lite.OpsSet.TFLITE_BUILTINS,   # fallback for unsupported ops
        ]
        converter.inference_input_type = tf.uint8
        converter.inference_output_type = tf.uint8
        if representative_dataset:
            converter.representative_dataset = representative_dataset
        print("  Calibrating on representative dataset...")
    else:
        # FP32 — no quantization, full precision
        converter.optimizations = []

    # Convert
    tflite_model = converter.convert()

    # Save
    with open(output_path, "wb") as f:
        f.write(tflite_model)

    size_mb = len(tflite_model) / 1e6
    print(f"✅ TFLite model saved: {output_path} ({size_mb:.1f} MB)")
    return output_path


# --- FP32 version (no quantization) ---
tflite_fp32_path = savedmodel_to_tflite(
    savedmodel_dir,
    output_path="mobilenetv2_fp32.tflite",
    quantize=False
)

# --- INT8 quantized version ---
# Need a representative dataset (100 sample images is enough)
def representative_dataset_gen():
    """Provide 100 sample inputs for INT8 calibration."""
    for _ in range(100):
        # In real use: load actual images from your dataset
        sample = np.random.randn(1, 224, 224, 3).astype(np.float32)
        # Normalize to [0, 1] — same preprocessing as inference
        sample = (sample - sample.min()) / (sample.max() - sample.min())
        yield [sample]

tflite_int8_path = savedmodel_to_tflite(
    savedmodel_dir,
    output_path="mobilenetv2_int8.tflite",
    quantize=True,
    representative_dataset=representative_dataset_gen
)
```

---

## 7. Step 6 — Compare Model Sizes

```python
# ── Size comparison ───────────────────────────────────
import os

models_info = {
    "PyTorch FP32 (.pth)": "mobilenetv2.pth",
    "ONNX FP32 (.onnx)":   "mobilenetv2.onnx",
    "TFLite FP32":         "mobilenetv2_fp32.tflite",
    "TFLite INT8":         "mobilenetv2_int8.tflite",
}

print(f"{'Model':<30} {'Size':>10}")
print("-" * 42)
for name, path in models_info.items():
    if os.path.exists(path):
        size = os.path.getsize(path) / 1e6
        print(f"{name:<30} {size:>8.1f} MB")
    else:
        print(f"{name:<30} {'(not found)':>10}")
```

**Expected output:**
```
Model                          Size
──────────────────────────────────────────
PyTorch FP32 (.pth)            14.0 MB
ONNX FP32 (.onnx)              14.0 MB
TFLite FP32                    14.0 MB
TFLite INT8                     3.6 MB   ← 4× smaller!
```

---

## 8. Step 7 — Validate TFLite Model Output

Cross-check that TFLite and PyTorch give the same predictions on the same input.

```python
# ── Validate TFLite matches PyTorch output ────────────
import torch
import numpy as np
import tensorflow as tf

def validate_tflite_vs_pytorch(pytorch_model, tflite_path, test_image_path=None):
    """Compare outputs of PyTorch and TFLite on same input."""

    # Create test input
    test_input_np = np.random.randn(1, 3, 224, 224).astype(np.float32)

    # PyTorch inference
    pytorch_model.eval()
    with torch.no_grad():
        pt_input = torch.from_numpy(test_input_np)
        pt_output = pytorch_model(pt_input).numpy()

    # TFLite inference (input is NHWC: batch, height, width, channels)
    tflite_input = test_input_np.transpose(0, 2, 3, 1)  # NCHW → NHWC

    interpreter = tf.lite.Interpreter(model_path=tflite_path)
    interpreter.allocate_tensors()

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    interpreter.set_tensor(input_details[0]['index'], tflite_input)
    interpreter.invoke()
    tflite_output = interpreter.get_tensor(output_details[0]['index'])

    # Compare top predictions
    pt_top5 = np.argsort(pt_output[0])[::-1][:5]
    tflite_top5 = np.argsort(tflite_output[0])[::-1][:5]

    print("PyTorch top-5 predictions  :", pt_top5.tolist())
    print("TFLite  top-5 predictions  :", tflite_top5.tolist())
    print(f"Top-1 match: {'✅' if pt_top5[0] == tflite_top5[0] else '❌'}")

    max_diff = np.max(np.abs(pt_output[0] - tflite_output[0]))
    print(f"Max output difference      : {max_diff:.6f}")

    if max_diff < 0.01:
        print("✅ Models match within acceptable tolerance")
    else:
        print("⚠️  Large output difference — check conversion steps")

validate_tflite_vs_pytorch(model, tflite_fp32_path)
```

---

## 9. Common Conversion Errors and Fixes

| Error | Cause | Fix |
|---|---|---|
| `Unsupported ONNX op: ...` | Op not in TFLite op set | Use `opset_version=11` or 12; avoid custom ops |
| `Shape mismatch` | NCHW vs NHWC mismatch | Add transpose layer or handle in Android code |
| `Dynamic shapes not supported` | Model uses variable input shapes | Set `dynamic_axes={}` in export |
| `INT8 calibration failed` | Representative dataset too small | Use 100–500 real images, not random noise |
| `Large accuracy drop after INT8` | Poor calibration data | Use real data matching inference distribution |
| `model.eval()` forgotten | BatchNorm in training mode | Always call `model.eval()` before export |

---

## 10. Alternative: Direct TFLite Export (If Model is in Keras/TF)

If your model is already in TensorFlow/Keras (not PyTorch), skip ONNX entirely:

```python
# Much simpler — direct TF → TFLite
import tensorflow as tf

# Load Keras model
keras_model = tf.keras.applications.MobileNetV2(weights="imagenet")

# Convert directly
converter = tf.lite.TFLiteConverter.from_keras_model(keras_model)
converter.optimizations = [tf.lite.Optimize.DEFAULT]  # INT8 quantization
tflite_model = converter.convert()

with open("mobilenetv2_keras.tflite", "wb") as f:
    f.write(tflite_model)
print("✅ Direct Keras → TFLite done")
```

---

## 11. Summary

```
PyTorch model
    ↓ torch.onnx.export() — opset 12
ONNX model
    ↓ onnx-tf prepare().export_graph()
TF SavedModel
    ↓ TFLiteConverter.from_saved_model()
TFLite FP32 model  (~14 MB for MobileNetV2)
    ↓ + representative_dataset + INT8 optimizations
TFLite INT8 model  (~3.6 MB — 4× smaller, ready for Android)
```

- Always `model.eval()` before export
- Always validate: ONNX → TFLite output must match PyTorch within 0.01
- INT8 quantization gives ~4× size reduction with <1% accuracy drop when calibrated on real data
- Use `opset_version=12` for best TFLite compatibility

---

## ➡️ Next Lesson

**[2.2 — Adding TFLite to an Android Project →](./02_add_tflite_to_android.md)**

---

*Module 2 · On-Device AI for Android Development*