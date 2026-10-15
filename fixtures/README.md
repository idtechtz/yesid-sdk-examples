# Fixtures

Synthetic images made for testing. They show a fictitious "specimen" card with made-up values: no real person, no real document number.

| File | Use |
|---|---|
| `card_single.jpg` | one card, the normal upload test |
| `card_two_cards.jpg` | two cards in one picture; the SDK must refuse it ("Keep only one card in view") |
| `card_blurred.jpg` | an unreadable picture; the SDK must show a clear error, not crash |

The server is not expected to accept a specimen as a valid national card, so the smoke tests check that the app reaches a result or a typed error screen, not that extraction succeeds.
