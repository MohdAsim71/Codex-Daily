# Dynamic Deeplink Notes

## Supported Deeplink Formats

https://www.example.com/rewards-program
https://www.example.com/rewards-program/gold-card
https://www.example.com/rewards-program/platinum
https://www.example.com/rewards-program/{dynamic-name}

--------------------------------------------------

# Deeplink Handling Logic

## 1. Rewards Program Listing

URL:
/rewards-program

Action:
Open Rewards Program Listing Screen

Example:
https://www.example.com/rewards-program

--------------------------------------------------

## 2. Dynamic Rewards Landing

URL:
/rewards-program/{rewardName}

Action:
Open Dynamic Rewards Landing Screen

Examples:
https://www.example.com/rewards-program/gold-card
https://www.example.com/rewards-program/platinum

--------------------------------------------------

# Important

Do NOT use:

uri.lastPathSegment == "rewards-program"

Reason:
For dynamic links, the lastPathSegment becomes:

gold-card
platinum
etc.

--------------------------------------------------

# Recommended Approach

Use:

val segments = uri.pathSegments

Then:

if (segments.isNotEmpty() && segments[0] == "rewards-program") {

    val rewardName = segments.getOrNull(1)

    if (rewardName.isNullOrEmpty()) {

        // Open Listing Screen

    } else {

        // Open Dynamic Landing Screen

    }
}

--------------------------------------------------

# Output Examples

Example 1

URL:
https://www.example.com/rewards-program

Result:
rewardName = null
→ Open Listing Screen

--------------------------------------------------

Example 2

URL:
https://www.example.com/rewards-program/gold-card

Result:
rewardName = gold-card
→ Open Dynamic Landing Screen

--------------------------------------------------

Example 3

URL:
https://www.example.com/rewards-program/platinum

Result:
rewardName = platinum
→ Open Dynamic Landing Screen

--------------------------------------------------

# Benefits

- Supports unlimited dynamic URLs
- Future-proof implementation
- Cleaner deeplink parsing
- Easy to maintain
- Single logic handles both listing and landing

--------------------------------------------------

# Commit Note

Added deeplink support for dynamic rewards program URLs.

Supported:
- /rewards-program
- /rewards-program/{dynamic-name}

Implemented dynamic path parsing using uri.pathSegments.