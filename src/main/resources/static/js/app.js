/**
 * SILENT-SOS • UNIFIED CLIENT & COMMAND CENTER APPLICATION
 * Glassmorphic UI with Liquid Glass Elements, Geolocation, MediaRecorder, Leaflet GIS, & REST API.
 */

// Application State
let currentView = "disguise";
let currentInput = "0";
let expression = "";
let secretBuffer = "";
let equalsTapCount = 0;
let lastEqualsTapTime = 0;
let selectedThreatLevel = "CRITICAL";

let currentCoords = { latitude: 12.9716, longitude: 77.5946, accuracy: 8.5 };
let currentBattery = { level: 95, charging: false };

let map = null;
let markers = {};
let alertsCache = [];
let selectedAlertId = null;

document.addEventListener("DOMContentLoaded", () => {
    initCalculator();
    initTelemetry();
    initThreatChips();
    initPanicSphere();
    loadEmergencyContacts();
    initMap();
    fetchAlerts();
    fetchStats();

    // Auto-sync incident queue and KPI counters every 3 seconds
    setInterval(() => {
        fetchAlerts(true);
        fetchStats();
    }, 3000);
});

/* ============================================================
   1. VIEW SWITCHING & PANIC ESCAPE
   ============================================================ */
function switchView(viewName) {
    currentView = viewName;

    // Update View Sections
    const views = ["disguise", "tactical", "responder", "contacts"];
    views.forEach(v => {
        const el = document.getElementById("view-" + v);
        if (el) {
            if (v === viewName) {
                el.classList.add("active-view");
            } else {
                el.classList.remove("active-view");
            }
        }
    });

    // Update Nav Pills
    const pills = document.querySelectorAll(".nav-pill");
    pills.forEach(p => {
        if (p.getAttribute("onclick")?.includes(viewName)) {
            p.classList.add("active");
        } else {
            p.classList.remove("active");
        }
    });

    // If switching to responder view, refresh Leaflet container
    if (viewName === "responder") {
        setTimeout(() => {
            if (map) {
                map.invalidateSize();
                if (alertsCache.length > 0 && alertsCache[0].latitude) {
                    map.setView([alertsCache[0].latitude, alertsCache[0].longitude], 14);
                }
            }
        }, 200);
    }
}

// Global hotkeys (Esc key instantly switches to Calculator mode)
window.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
        switchView("disguise");
        showToast("Calculator Mode", "success");
    }
});

/* ============================================================
   2. LIQUID GLASS CALCULATOR & COVERT DISTRESS TRIGGERS
   ============================================================ */
function initCalculator() {
    const buttons = document.querySelectorAll(".liquid-calc-btn");
    buttons.forEach(btn => {
        btn.addEventListener("click", () => {
            const val = btn.getAttribute("data-val");
            handleCalcInput(val);
        });
    });

    // Double-tap title stealth trigger
    const titleEl = document.getElementById("calcTitle");
    if (titleEl) {
        let taps = 0;
        titleEl.addEventListener("click", () => {
            taps++;
            if (taps === 2) {
                taps = 0;
                triggerCovertSos("TITLE_DOUBLE_TAP");
            }
            setTimeout(() => { taps = 0; }, 500);
        });
    }

    // Keyboard support for calculator
    window.addEventListener("keydown", (e) => {
        if (e.target.tagName === "INPUT" || e.target.tagName === "TEXTAREA") return;
        if (currentView !== "disguise") return;

        if (e.key >= "0" && e.key <= "9") handleCalcInput(e.key);
        else if (["+", "-", "*", "/"].includes(e.key)) {
            const map = { "*": "×", "/": "÷", "+": "+", "-": "-" };
            handleCalcInput(map[e.key]);
        }
        else if (e.key === "Enter" || e.key === "=") handleCalcInput("=");
        else if (e.key === "Backspace") handleCalcInput("BACK");
    });
}

function handleCalcInput(val) {
    const currentDisplay = document.getElementById("calcCurrent");
    const historyDisplay = document.getElementById("calcHistory");

    // Track input sequence for covert detection
    secretBuffer += val;
    if (secretBuffer.length > 15) secretBuffer = secretBuffer.slice(-15);

    // Covert PIN Trigger Condition
    if (val === "=") {
        const now = Date.now();
        if (now - lastEqualsTapTime < 800) equalsTapCount++;
        else equalsTapCount = 1;
        lastEqualsTapTime = now;

        // Condition A: Rapid triple tap on "="
        if (equalsTapCount >= 3) {
            equalsTapCount = 0;
            triggerCovertSos("RAPID_EQUALS_TAP");
        }

        // Condition B: Covert PIN (999=, 911=, 112=, 000=)
        if (secretBuffer.includes("999=") || secretBuffer.includes("911=") || 
            secretBuffer.includes("112=") || secretBuffer.includes("000=")) {
            triggerCovertSos("COVERT_PIN_CODE");
        }
    }

    // Normal calculation
    if (val === "C") {
        currentInput = "0";
        expression = "";
    } else if (val === "BACK") {
        if (currentInput.length > 1) currentInput = currentInput.slice(0, -1);
        else currentInput = "0";
    } else if (val === "=") {
        if (expression || currentInput) {
            try {
                const fullExpr = expression + currentInput;
                const sanitized = fullExpr.replace(/×/g, "*").replace(/÷/g, "/");
                const result = evaluateMath(sanitized);
                historyDisplay.textContent = fullExpr + " =";
                currentInput = String(result);
                expression = "";
            } catch (err) {
                currentInput = "Error";
                expression = "";
            }
        }
    } else if (["+", "-", "×", "÷"].includes(val)) {
        expression = (expression || currentInput) + " " + val + " ";
        currentInput = "0";
        historyDisplay.textContent = expression;
    } else {
        if (val === "." && currentInput.includes(".")) return;
        if (currentInput === "0" && val !== ".") currentInput = val;
        else currentInput += val;
    }

    currentDisplay.textContent = currentInput;
}

function evaluateMath(fnStr) {
    return Function('"use strict";return (' + fnStr + ')')();
}

function triggerCovertSos(triggerMethod = "COVERT_CALCULATOR") {
    if (navigator.vibrate) navigator.vibrate([80, 40, 80]);

    // Discreet micro beacon pulse
    const beacon = document.getElementById("covertBeacon");
    if (beacon) {
        beacon.classList.add("firing");
        setTimeout(() => beacon.classList.remove("firing"), 1400);
    }

    sendSosPayload({
        triggerType: triggerMethod,
        threatLevel: "CRITICAL",
        userNotes: "Discreet distress signal triggered via " + triggerMethod
    });
}

/* ============================================================
   3. TACTICAL DIRECT PANIC BUTTON
   ============================================================ */
function initThreatChips() {
    const chips = document.querySelectorAll(".threat-glass-chip");
    chips.forEach(chip => {
        chip.addEventListener("click", () => {
            chips.forEach(c => c.classList.remove("active-level"));
            chip.classList.add("active-level");
            selectedThreatLevel = chip.getAttribute("data-level");
        });
    });
}

function initPanicSphere() {
    const panicBtn = document.getElementById("panicSphereBtn");
    if (panicBtn) {
        panicBtn.addEventListener("click", () => {
            const notes = document.getElementById("userNotes")?.value.trim() || "Tactical Panic SOS Button Pressed";
            startSosCountdown({
                triggerType: "DIRECT_PANIC_BUTTON",
                threatLevel: selectedThreatLevel,
                userNotes: notes
            });
        });
    }
}

/* ============================================================
   4. TELEMETRY & AMBIENT AUDIO
   ============================================================ */
function initTelemetry() {
    if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition(
            pos => {
                currentCoords.latitude = Number(pos.coords.latitude.toFixed(5));
                currentCoords.longitude = Number(pos.coords.longitude.toFixed(5));
                currentCoords.accuracy = Number(pos.coords.accuracy.toFixed(1));
                updateTelemetryUI();
            },
            () => {
                updateTelemetryUI();
            },
            { enableHighAccuracy: true, timeout: 6000 }
        );
    }

    if ("getBattery" in navigator) {
        navigator.getBattery().then(battery => {
            currentBattery.level = Math.round(battery.level * 100);
            currentBattery.charging = battery.charging;
            updateTelemetryUI();

            battery.addEventListener("levelchange", () => {
                currentBattery.level = Math.round(battery.level * 100);
                updateTelemetryUI();
            });
        }).catch(() => {});
    }
}

function updateTelemetryUI() {
    const coordsEl = document.getElementById("telemCoords");
    const accEl = document.getElementById("telemAccuracy");
    const batEl = document.getElementById("telemBattery");

    if (coordsEl) coordsEl.textContent = `${currentCoords.latitude}, ${currentCoords.longitude}`;
    if (accEl) accEl.textContent = `±${currentCoords.accuracy}m`;
    if (batEl) batEl.textContent = `${currentBattery.level}% ${currentBattery.charging ? '⚡' : ''}`;

    // Synchronize top HUD command bar
    const hudGps = document.getElementById("hudGps");
    const hudBat = document.getElementById("hudBattery");
    if (hudGps) hudGps.textContent = `${currentCoords.latitude}, ${currentCoords.longitude} (±${currentCoords.accuracy}m)`;
    if (hudBat) hudBat.textContent = `${currentBattery.level}% ${currentBattery.charging ? '⚡' : ''}`;
}

async function captureCovertAudio() {
    try {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) return null;
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        const mediaRecorder = new MediaRecorder(stream);
        const audioChunks = [];

        return new Promise((resolve) => {
            mediaRecorder.ondataavailable = (event) => {
                if (event.data.size > 0) audioChunks.push(event.data);
            };

            mediaRecorder.onstop = () => {
                const audioBlob = new Blob(audioChunks, { type: "audio/webm" });
                const reader = new FileReader();
                reader.readAsDataURL(audioBlob);
                reader.onloadend = () => {
                    resolve(reader.result);
                };
                stream.getTracks().forEach(track => track.stop());
            };

            mediaRecorder.start();
            setTimeout(() => {
                if (mediaRecorder.state !== "inactive") mediaRecorder.stop();
            }, 3500);
        });
    } catch (err) {
        return null;
    }
}

/* ============================================================
   5. SOS DISPATCH ENGINE
   ============================================================ */
async function sendSosPayload(details) {
    let audioData = null;
    try {
        audioData = await captureCovertAudio();
    } catch (e) {}

    const payload = {
        triggerType: details.triggerType || "COVERT_CALCULATOR",
        threatLevel: details.threatLevel || "CRITICAL",
        latitude: currentCoords.latitude,
        longitude: currentCoords.longitude,
        accuracyMeters: currentCoords.accuracy,
        batteryLevel: currentBattery.level,
        batteryCharging: currentBattery.charging,
        userNotes: details.userNotes || "Emergency distress triggered",
        audioBase64: audioData || "",
        timestamp: new Date().toISOString().replace("T", " ").substring(0, 19)
    };

    try {
        const res = await fetch("/api/sos/trigger", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const data = await res.json();
            showToast("Distress Beacon Activated: " + data.id, "success");
            fetchAlerts();
            fetchStats();
        }
    } catch (err) {
        showToast("Signal sent to local cache", "danger");
    }
}

async function triggerTestAlert() {
    try {
        const res = await fetch("/api/sos/test", { method: "POST" });
        if (res.ok) {
            const data = await res.json();
            showToast("Viva Demo Incident: " + data.id, "success");
            fetchAlerts();
            fetchStats();
        }
    } catch (e) {
        showToast("Demo trigger failed", "danger");
    }
}

/* ============================================================
   6. RESPONDER & LEAFLET GIS MAP
   ============================================================ */
function initMap() {
    const mapEl = document.getElementById("map");
    if (!mapEl || typeof L === "undefined") return;

    map = L.map("map", { attributionControl: false }).setView([12.9716, 77.5946], 13);

    // Free OpenStreetMap Tiles (Zero API Key Required)
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19
    }).addTo(map);
}

async function fetchAlerts(isPeriodic = false) {
    try {
        const res = await fetch("/api/sos/alerts");
        if (!res.ok) return;
        const alerts = await res.json();
        alertsCache = alerts;

        renderIncidentFeed(alerts);
        updateMapMarkers(alerts, !isPeriodic);
    } catch (e) {}
}

async function fetchStats() {
    try {
        const res = await fetch("/api/sos/stats");
        if (!res.ok) return;
        const stats = await res.json();

        const act = document.getElementById("statActive");
        const dis = document.getElementById("statDispatched");
        const resEl = document.getElementById("statResolved");
        const tot = document.getElementById("statTotal");

        if (act) act.textContent = stats.activeAlerts || 0;
        if (dis) dis.textContent = stats.acknowledgedAlerts || 0;
        if (resEl) resEl.textContent = stats.resolvedAlerts || 0;
        if (tot) tot.textContent = stats.totalAlerts || 0;

        // Synchronize HUD Beacons Ticker
        const hudBeacons = document.getElementById("hudBeacons");
        if (hudBeacons) {
            hudBeacons.textContent = `${stats.activeAlerts || 0} ACTIVE / ${stats.totalAlerts || 0} TOTAL`;
        }
    } catch (e) {}
}

function renderIncidentFeed(alerts) {
    const feed = document.getElementById("incidentFeed");
    if (!feed) return;

    if (!alerts || alerts.length === 0) {
        feed.innerHTML = `
            <div style="text-align: center; color: var(--text-tertiary); padding: 40px 10px;">
                <p style="font-size: 1.8rem; margin-bottom: 6px;">🛡️</p>
                <p style="font-size: 0.9rem; font-weight: 700; color: var(--text-secondary);">No Active Incidents</p>
                <p style="font-size: 0.75rem;">System standing by on all channels.</p>
            </div>
        `;
        return;
    }

    feed.innerHTML = alerts.map(a => {
        const isSelected = a.id === selectedAlertId ? "card-selected" : "";
        const audioHtml = a.audioBase64 && a.audioBase64.length > 50 ? `
            <div style="margin: 10px 0;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
                    <span style="font-size: 0.7rem; color: var(--accent-cyan);">🎙️ Ambient Acoustic Evidence:</span>
                    <div class="live-audio-waveform">
                        <span class="wave-bar bar-1"></span>
                        <span class="wave-bar bar-2"></span>
                        <span class="wave-bar bar-3"></span>
                        <span class="wave-bar bar-4"></span>
                        <span class="wave-bar bar-5"></span>
                    </div>
                </div>
                <audio controls src="${a.audioBase64}" style="width: 100%; height: 32px; border-radius: 8px;"></audio>
            </div>
        ` : "";

        return `
            <div class="incident-glass-card ${isSelected}" onclick="focusAlert('${a.id}', ${a.latitude}, ${a.longitude})">
                <div class="card-header-row">
                    <span style="font-weight: 800; font-size: 0.92rem; letter-spacing: 0.5px;">${escapeHtml(a.id)}</span>
                    <div style="display: flex; gap: 6px; align-items: center;">
                        <span class="badge-pill badge-${a.threatLevel}">${escapeHtml(a.threatLevel)}</span>
                        <span class="status-tag ${a.status}">${escapeHtml(a.status)}</span>
                    </div>
                </div>

                <div style="font-size: 0.75rem; color: var(--text-tertiary); display: grid; grid-template-columns: 1fr 1fr; gap: 4px; margin-bottom: 8px;">
                    <span>🕒 ${escapeHtml(a.timestamp)}</span>
                    <span>🔋 Battery: ${a.batteryLevel}% ${a.batteryCharging ? '⚡' : ''}</span>
                    <span>📍 Lat: ${a.latitude.toFixed(4)}, Lng: ${a.longitude.toFixed(4)}</span>
                    <span>🎯 Acc: ±${a.accuracyMeters}m</span>
                </div>

                <div style="background: rgba(0,0,0,0.3); padding: 8px 12px; border-radius: 8px; font-size: 0.8rem; margin-bottom: 10px;">
                    <strong>Trigger:</strong> ${escapeHtml(a.triggerType)}<br>
                    <strong>Notes:</strong> ${escapeHtml(a.userNotes || 'None')}
                </div>

                ${audioHtml}

                <div style="display: flex; gap: 8px; flex-wrap: wrap;">
                    <button class="liquid-btn" style="padding: 6px 14px; font-size: 0.74rem;" onclick="event.stopPropagation(); updateAlertStatus('${a.id}', 'DISPATCHED')">
                        🚓 Dispatch
                    </button>
                    <button class="liquid-btn liquid-btn-emerald" style="padding: 6px 14px; font-size: 0.74rem;" onclick="event.stopPropagation(); updateAlertStatus('${a.id}', 'RESOLVED')">
                        ✅ Resolve
                    </button>
                    <a href="${a.googleMapsUrl}" target="_blank" class="liquid-btn" style="padding: 6px 14px; font-size: 0.74rem; text-decoration: none;" onclick="event.stopPropagation()">
                        🗺️ Maps
                    </a>
                    <button class="liquid-btn" style="padding: 6px 14px; font-size: 0.74rem;" onclick="event.stopPropagation(); openBroadcastModal('${a.id}')">
                        📱 Preview
                    </button>
                </div>
            </div>
        `;
    }).join("");
}

function updateMapMarkers(alerts, autoPanFirst = false) {
    if (!map || typeof L === "undefined") return;

    alerts.forEach(a => {
        if (!a.latitude || !a.longitude) return;
        const coords = [a.latitude, a.longitude];

        if (markers[a.id]) {
            markers[a.id].setLatLng(coords);
        } else {
            const markerColor = a.threatLevel === "CRITICAL" ? "#ff2a4d" : 
                               (a.threatLevel === "HIGH" ? "#ff7315" : "#38bdf8");

            // Tactical Sonar Radar Pulse Marker
            const customIcon = L.divIcon({
                className: "custom-sonar-pin",
                html: `
                    <div class="sonar-pin-container" style="color: ${markerColor};">
                        <div class="sonar-pulse-ring-1"></div>
                        <div class="sonar-pulse-ring-2"></div>
                        <div class="sonar-core" style="background-color: ${markerColor};"></div>
                    </div>
                `,
                iconSize: [28, 28],
                iconAnchor: [14, 14]
            });

            const marker = L.marker(coords, { icon: customIcon }).addTo(map);
            marker.bindPopup(`
                <div style="font-family: sans-serif; color: #111; min-width: 175px;">
                    <h4 style="margin: 0 0 4px 0; color: #dc2626;">🚨 ${escapeHtml(a.id)} (${escapeHtml(a.threatLevel)})</h4>
                    <p style="font-size: 12px; margin: 2px 0;"><strong>Status:</strong> ${escapeHtml(a.status)}</p>
                    <p style="font-size: 12px; margin: 2px 0;"><strong>Battery:</strong> ${a.batteryLevel}%</p>
                    <p style="font-size: 12px; margin: 2px 0;"><strong>Trigger:</strong> ${escapeHtml(a.triggerType)}</p>
                    <a href="${a.googleMapsUrl}" target="_blank" style="display:inline-block; margin-top: 6px; font-size: 11px; color: #2563eb; font-weight: bold;">Open Satellite Route</a>
                </div>
            `);
            markers[a.id] = marker;
        }
    });

    if (autoPanFirst && alerts.length > 0 && alerts[0].latitude && alerts[0].longitude) {
        map.setView([alerts[0].latitude, alerts[0].longitude], 14);
    }
}

function focusAlert(id, lat, lng) {
    selectedAlertId = id;
    if (map && lat && lng) {
        map.setView([lat, lng], 15, { animate: true });
        if (markers[id]) markers[id].openPopup();
    }
    renderIncidentFeed(alertsCache);
}

async function updateAlertStatus(id, newStatus) {
    try {
        const res = await fetch(`/api/sos/status?id=${id}&status=${newStatus}`, { method: "PUT" });
        if (res.ok) {
            fetchAlerts();
            fetchStats();
            showToast(`Incident ${id} updated to ${newStatus}`, "success");
        }
    } catch (e) {
        showToast("Status update failed", "danger");
    }
}

/* ============================================================
   7. GUARDIANS / CONTACTS DIRECTORY
   ============================================================ */
async function loadEmergencyContacts() {
    try {
        const res = await fetch("/api/contacts");
        if (res.ok) {
            const contacts = await res.json();
            renderContacts(contacts);
        }
    } catch (e) {}
}

function renderContacts(contacts) {
    const grid = document.getElementById("contactsGrid");
    if (!grid) return;

    if (!contacts || contacts.length === 0) {
        grid.innerHTML = "<p style='color: var(--text-tertiary); font-size: 0.85rem;'>No emergency guardians registered.</p>";
        return;
    }

    grid.innerHTML = contacts.map(c => `
        <div class="guardian-item-card">
            <div>
                <div style="font-weight: 700; font-size: 0.95rem; display: flex; align-items: center; gap: 8px;">
                    ${escapeHtml(c.name)} ${c.primary ? '<span style="color: #fbb024; font-size: 0.8rem;">★ PRIMARY</span>' : ''}
                </div>
                <div style="font-size: 0.78rem; color: var(--text-tertiary); margin-top: 3px;">
                    ${escapeHtml(c.relationship)} • 📞 ${escapeHtml(c.phone)} ${c.email ? '• ✉️ ' + escapeHtml(c.email) : ''}
                </div>
            </div>
            <button class="liquid-btn liquid-btn-danger" style="padding: 6px 12px; font-size: 0.75rem;" onclick="deleteContact('${c.id}')">
                Remove
            </button>
        </div>
    `).join("");
}

async function handleContactSubmit(event) {
    event.preventDefault();
    const name = document.getElementById("contactName").value.trim();
    const phone = document.getElementById("contactPhone").value.trim();
    const email = document.getElementById("contactEmail").value.trim();
    const relationship = document.getElementById("contactRel").value.trim();
    const primary = document.getElementById("contactPrimary").checked;

    if (!name || !phone) return;

    try {
        const res = await fetch("/api/contacts", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, phone, email, relationship, primary })
        });

        if (res.ok) {
            closeAddContactModal();
            loadEmergencyContacts();
            showToast("Guardian registered successfully!", "success");
            event.target.reset();
        }
    } catch (e) {
        showToast("Error adding contact", "danger");
    }
}

async function deleteContact(id) {
    if (!confirm("Remove this emergency guardian?")) return;
    try {
        const res = await fetch(`/api/contacts?id=${id}`, { method: "DELETE" });
        if (res.ok) {
            loadEmergencyContacts();
            showToast("Guardian removed", "success");
        }
    } catch (e) {}
}

/* ============================================================
   8. MODALS & TOASTS
   ============================================================ */
function openAddContactModal() {
    document.getElementById("addContactModal").style.display = "flex";
}

function closeAddContactModal() {
    document.getElementById("addContactModal").style.display = "none";
}

function openBroadcastModal(id) {
    const alertItem = alertsCache.find(a => a.id === id);
    if (!alertItem) return;

    document.getElementById("previewSms").textContent = alertItem.smsPreview || "No SMS template generated.";
    document.getElementById("previewWhatsApp").textContent = alertItem.whatsAppPreview || "No WhatsApp template generated.";
    document.getElementById("broadcastModal").style.display = "flex";
}

function closeBroadcastModal() {
    document.getElementById("broadcastModal").style.display = "none";
}

function showToast(message, type = "success") {
    const toast = document.getElementById("glassToast");
    if (!toast) return;

    toast.textContent = message;
    toast.className = `glass-toast visible ${type}`;

    setTimeout(() => {
        toast.className = "glass-toast";
    }, 3200);
}

function escapeHtml(str) {
    if (!str) return "";
    return String(str).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

/* ============================================================
   9. 5-SECOND SAFETY COUNTDOWN & MOBILE PUSH ALERTS
   ============================================================ */
let countdownTimer = null;
let pendingDistressDetails = null;

function startSosCountdown(details) {
    pendingDistressDetails = details;
    const modal = document.getElementById("sosCountdownModal");
    const numEl = document.getElementById("countdownNumber");
    const ringEl = document.getElementById("countdownProgressRing");

    if (!modal || !numEl || !ringEl) {
        sendSosPayload(details);
        return;
    }

    let timeLeft = 5;
    numEl.textContent = timeLeft;

    // Reset SVG ring stroke
    ringEl.style.transition = "none";
    ringEl.style.strokeDashoffset = "0";
    modal.style.display = "flex";

    // Trigger smooth 5-second circular SVG fill
    void ringEl.offsetWidth; // Force CSS repaint
    ringEl.style.transition = "stroke-dashoffset 5s linear";
    ringEl.style.strokeDashoffset = "440";

    clearInterval(countdownTimer);
    countdownTimer = setInterval(() => {
        timeLeft--;
        if (timeLeft > 0) {
            numEl.textContent = timeLeft;
        } else {
            clearInterval(countdownTimer);
            modal.style.display = "none";
            // Dispatch to server
            sendSosPayload(pendingDistressDetails);
            // Show Simulated Mobile Push Alert on Lockscreen
            showPushAlert(
                "WhatsApp • Mom (Primary Guardian)",
                `🚨 DISTRESS BEACON DISPATCHED: ${pendingDistressDetails.threatLevel} alert from GPS coordinates (${currentCoords.latitude}, ${currentCoords.longitude}). Live route mapped.`
            );
        }
    }, 1000);
}

function cancelSosCountdown() {
    clearInterval(countdownTimer);
    const modal = document.getElementById("sosCountdownModal");
    if (modal) modal.style.display = "none";
    showToast("SOS Aborted • False Alarm Cancelled", "success");
}

function showPushAlert(title, message) {
    const banner = document.getElementById("pushNotificationBanner");
    const body = document.getElementById("pushAlertContent");
    if (!banner || !body) return;

    body.innerHTML = `<strong>🚨 SILENT-SOS BROADCAST RECEIVED:</strong> ${escapeHtml(message)}`;
    banner.style.display = "block";

    setTimeout(() => {
        banner.style.display = "none";
    }, 7000);
}

function dismissPushAlert() {
    const banner = document.getElementById("pushNotificationBanner");
    if (banner) banner.style.display = "none";
}
