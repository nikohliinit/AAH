import os
import threading

from flask import Flask, jsonify, redirect, render_template, url_for

from screener import cache
from screener.rank import run_screen
from scheduler import start_scheduler

app = Flask(__name__)
app.jinja_env.auto_reload = True

_scan_lock = threading.Lock()
_scan_running = False


def _run_scan_background():
    global _scan_running
    with _scan_lock:
        if _scan_running:
            return
        _scan_running = True
    try:
        result = run_screen()
        cache.save_latest(result)
    finally:
        _scan_running = False


@app.route("/")
def index():
    result = cache.load_latest()
    return render_template("index.html", result=result, scanning=_scan_running)


@app.route("/api/top")
def api_top():
    result = cache.load_latest()
    return jsonify(result or {})


@app.route("/api/status")
def api_status():
    return jsonify({"scanning": _scan_running})


@app.route("/refresh", methods=["POST"])
def refresh():
    if not _scan_running:
        threading.Thread(target=_run_scan_background, daemon=True).start()
    return redirect(url_for("index"))


if __name__ == "__main__":
    DEBUG = False
    # Guard against Flask's debug-mode reloader starting the scheduler twice:
    # the reloader's parent watcher process has no WERKZEUG_RUN_MAIN set.
    if not DEBUG or os.environ.get("WERKZEUG_RUN_MAIN") == "true":
        start_scheduler(hour=7, minute=30)
    app.run(host="127.0.0.1", port=5057, debug=DEBUG)
