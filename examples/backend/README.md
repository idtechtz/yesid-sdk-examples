# Reference backend

Creates a Yesid session for the sample apps. The secret API key stays here, never in the app.

```bash
YESID_API_KEY=... YESID_CONSUMER_USERNAME=... node server.js    # Node 18 or newer, no dependencies
curl -X POST localhost:8787/session                             # -> {"sessionId":"..."}
```
Optional: `YESID_API_BASE` (default sandbox), `PORT` (default 8787). Change the `steps` object in `server.js` to change what a session asks the user to do. Put your keys in a git-ignored `.env.local` if you like.
