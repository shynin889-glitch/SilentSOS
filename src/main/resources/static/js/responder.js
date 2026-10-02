/**
 * Silent-SOS Responder & Incident Management System
 * Interactive Leaflet.js live map, real-time alert triage, and audio evidence playback.
 */

let map = null;
let markers = {};
let alertsCache = [];
let selectedAlertId = null;

document.addEventListener("DOMContentLoaded", () => {
    initMap();
    fetchAlerts();
    fetchStats();

    // Auto refresh every 3 seconds
    setInterval(() => {
        fetchAlerts(true);
        fetchStats();
    }, 3000);
});

/* ============================================================
   1. LEAFLET MAP INITIALIZATION
   ============================================================ */
function initMap() {
    // Default center (Bangalore Tech Hub / India center fallback)
    const defaultLat = 12.9716;
    const defaultLng = 77.5946;

    if (typeof L !== "undefined") {
        map = L.map("map", {
            attributionControl: false
        }).setView([defaultLat, defaultLng], 13);

        // Dark Matter / High Contrast OpenStreetMap Tiles
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19
        }).addTo(map);
    } else {
        console.warn("Leaflet library not loaded or offline. Fallback map view.");
    }
}

/* ============================================================
   2. FETCH ALERTS & STATS FROM JAVA SERVER
   ============================================================ */
async function fetchAlerts(isPeriodic = false) {
    try {
        const res = await fetch("/api/sos/alerts");
        if (!res.ok) return;

        const alerts = await res.json();
        alertsCache = alerts;

        renderIncidentFeed(alerts);
        updateMapMarkers(alerts, !isPeriodic);
    } catch (e) {
        console.error("Error fetching alerts:", e);
    }
}

async function fetchStats() {
    try {
        const res = await fetch("/api/sos/stats");
        if (!res.ok) return;
        const stats = await res.json();

        document.getElementById("statActive").textContent = stats.activeAlerts || 0;
        document.getElementById("statDispatched").textContent = stats.acknowledgedAlerts || 0;
        document.getElementById("statResolved").textContent = stats.resolvedAlerts || 0;
        document.getElementById("statTotal").textContent = stats.totalAlerts || 0;
    } catch (e) {
        console.warn("Error fetching stats:", e);
    }
}

/* ============================================================
   3. RENDER INCIDENT FEED
   ============================================================ */
function renderIncidentFeed(alerts) {
    const feed = document.getElementById("incidentFeed");
    if (!feed) return;

    if (!alerts || alerts.length === 0) {
        feed.innerHTML = `
            <div style="text-align: center; color: var(--text-secondary); padding: 40px 10px;">
                <p style="font-size: 1.5rem; margin-bottom: 8px;">🛡️</p>
                <p style="font-size: 0.9rem; font-weight: 600;">No Emergency Incidents</p>
                <p style="font-size: 0.75rem;">System standing by on all channels.</p>
            </div>
        `;
        return;
    }

    feed.innerHTML = alerts.map(a => {
        const isSelected = a.id === selectedAlertId ? "active-selected" : "";
        const audioHtml = a.audioBase64 && a.audioBase64.length > 50 ? `
            <div class="audio-box">
                <span style="font-size: 0.7rem; color: #38bdf8; display: block; margin-bottom: 2px;">🎙️ Covert Ambient Audio Evidence:</span>
                <audio controls src="${a.audioBase64}"></audio>
            </div>
        ` : "";

        return `
            <div class="incident-card ${isSelected}" onclick="focusAlert('${a.id}', ${a.latitude}, ${a.longitude})">
                <div class="card-top">
                    <span class="incident-id">${escapeHtml(a.id)}</span>
                    <div>
                        <span class="badge badge-${a.threatLevel}">${escapeHtml(a.threatLevel)}</span>
                        <span class="status-badge ${a.status}">${escapeHtml(a.status)}</span>
                    </div>
                </div>

                <div class="card-meta">
                    <span>🕒 ${escapeHtml(a.timestamp)}</span>
                    <span>🔋 Battery: ${a.batteryLevel}% ${a.batteryCharging ? '⚡' : ''}</span>
                    <span>📍 Lat: ${a.latitude.toFixed(4)}, Lng: ${a.longitude.toFixed(4)}</span>
                    <span>🎯 Acc: ±${a.accuracyMeters}m</span>
                </div>

                <div class="card-notes">
                    <strong>Trigger:</strong> ${escapeHtml(a.triggerType)}<br>
                    <strong>Notes:</strong> ${escapeHtml(a.userNotes || 'None')}
                </div>

                ${audioHtml}

                <div class="card-actions">
                    <button class="btn-triage dispatch" onclick="event.stopPropagation(); updateStatus('${a.id}', 'DISPATCHED')">
                        🚓 Dispatch Unit
                    </button>
                    <button class="btn-triage resolve" onclick="event.stopPropagation(); updateStatus('${a.id}', 'RESOLVED')">
                        ✅ Resolve
                    </button>
                    <a href="${a.googleMapsUrl}" target="_blank" class="btn-triage" onclick="event.stopPropagation()" style="text-decoration: none;">
                        🗺️ Maps
                    </a>
                    <button class="btn-triage" onclick="event.stopPropagation(); showDispatchPreview('${a.id}')">
                        📱 Broadcasts
                    </button>
                </div>
            </div>
        `;
    }).join("");
}

/* ============================================================
   4. UPDATE MAP MARKERS & POPUPS
   ============================================================ */
function updateMapMarkers(alerts, autoPanFirst = false) {
    if (!map || typeof L === "undefined") return;

    alerts.forEach(a => {
        if (!a.latitude || !a.longitude) return;

        const coords = [a.latitude, a.longitude];

        if (markers[a.id]) {
            // Update marker position
            markers[a.id].setLatLng(coords);
        } else {
            // Create new marker
            const markerColor = a.threatLevel === "CRITICAL" ? "#ef4444" : 
                               (a.threatLevel === "HIGH" ? "#f97316" : "#3b82f6");

            const customIcon = L.divIcon({
                className: "custom-sos-pin",
                html: `<div style="background-color: ${markerColor}; width: 18px; height: 18px; border-radius: 50%; border: 3px solid #fff; box-shadow: 0 0 10px ${markerColor};"></div>`,
                iconSize: [20, 20],
                iconAnchor: [10, 10]
            });

            const marker = L.marker(coords, { icon: customIcon }).addTo(map);

            const popupContent = `
                <div style="font-family: sans-serif; color: #111; min-width: 180px;">
                    <h4 style="margin: 0 0 4px 0; color: #dc2626;">🚨 ${escapeHtml(a.id)} (${escapeHtml(a.threatLevel)})</h4>
                    <p style="font-size: 12px; margin: 2px 0;"><strong>Status:</strong> ${escapeHtml(a.status)}</p>
                    <p style="font-size: 12px; margin: 2px 0;"><strong>Battery:</strong> ${a.batteryLevel}%</p>
                    <p style="font-size: 12px; margin: 2px 0;"><strong>Trigger:</strong> ${escapeHtml(a.triggerType)}</p>
                    <a href="${a.googleMapsUrl}" target="_blank" style="display:inline-block; margin-top: 6px; font-size: 11px; color: #2563eb; font-weight: bold;">Open Google Navigation</a>
                </div>
            `;
            marker.bindPopup(popupContent);
            markers[a.id] = marker;
        }
    });

    if (autoPanFirst && alerts.length > 0 && alerts[0].latitude && alerts[0].longitude) {
        map.setView([alerts[0].latitude, alerts[0].longitude], 15);
    }
}

function focusAlert(id, lat, lng) {
    selectedAlertId = id;
    if (map && lat && lng) {
        map.setView([lat, lng], 16, { animate: true });
        if (markers[id]) {
            markers[id].openPopup();
        }
    }
    renderIncidentFeed(alertsCache);
}

/* ============================================================
   5. TRIAGE ACTIONS
   ============================================================ */
async function updateStatus(id, newStatus) {
    try {
        const res = await fetch(`/api/sos/status?id=${id}&status=${newStatus}`, { method: "PUT" });
        if (res.ok) {
            fetchAlerts();
            fetchStats();
        }
    } catch (e) {
        alert("Failed to update status: " + e.message);
    }
}

async function triggerDemoBeacon() {
    try {
        const res = await fetch("/api/sos/test", { method: "POST" });
        if (res.ok) {
            fetchAlerts();
            fetchStats();
        }
    } catch (e) {
        alert("Demo test beacon failed: " + e.message);
    }
}

function showDispatchPreview(id) {
    const alertItem = alertsCache.find(a => a.id === id);
    if (!alertItem) return;

    const modal = document.getElementById("broadcastModal");
    const smsEl = document.getElementById("previewSms");
    const waEl = document.getElementById("previewWhatsApp");

    smsEl.textContent = alertItem.smsPreview || "No SMS broadcast template generated.";
    waEl.textContent = alertItem.whatsAppPreview || "No WhatsApp template generated.";

    modal.style.display = "flex";
}

function closeBroadcastModal() {
    document.getElementById("broadcastModal").style.display = "none";
}

function escapeHtml(str) {
    if (!str) return "";
    return String(str).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}
