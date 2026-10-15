// Reference backend: creates a Yesid session for the sample app.
// The secret API key stays HERE (environment variable), never in the mobile app.
//
//   YESID_API_KEY=... YESID_CONSUMER_USERNAME=... node server.js
//
// POST /session  ->  {"sessionId": "..."}
// Needs Node 18 or newer (global fetch). No dependencies.
const http = require('http');

const API_BASE = (process.env.YESID_API_BASE || 'https://api.sandbox.yesid.tech/api').replace(/\/$/, '');
const API_KEY = process.env.YESID_API_KEY;
const CONSUMER = process.env.YESID_CONSUMER_USERNAME;
const PORT = Number(process.env.PORT || 8787);

if (!API_KEY) { console.error('Set YESID_API_KEY'); process.exit(1); }

// What the session asks the user to do. Change the steps to fit your flow.
const steps = {
  documentCapture: { enabled: true, captureMode: 'both', ocrEnabled: true, sourceMode: 'both', maxAttempts: 5 },
  liveness: { enabled: true },
  faceMatch: { enabled: true },
};

async function createSession() {
  const res = await fetch(`${API_BASE}/v1/sdk/session`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
      'X-API-Key': API_KEY,
      ...(CONSUMER ? { 'X-Consumer-Username': CONSUMER } : {}),
    },
    body: JSON.stringify({ referenceId: String(Date.now()), ttlMinutes: 1440, allowResubmission: true, steps }),
  });
  const text = await res.text();
  if (!res.ok) throw new Error(`Yesid API answered HTTP ${res.status}`);
  return JSON.parse(text);
}

http.createServer(async (req, res) => {
  if (req.method !== 'POST' || req.url !== '/session') { res.writeHead(404).end(); return; }
  try {
    const { sessionId } = await createSession();
    res.writeHead(200, { 'Content-Type': 'application/json' }).end(JSON.stringify({ sessionId }));
  } catch (e) {
    console.error(e.message);
    res.writeHead(502, { 'Content-Type': 'application/json' }).end(JSON.stringify({ error: e.message }));
  }
}).listen(PORT, () => console.log(`session endpoint on http://localhost:${PORT}/session (API ${API_BASE})`));
