"""Daily pre-market scan + macOS notification with the top 5.

Runs inside the same process as the Flask app (see app.py) via APScheduler,
so a single `python app.py` gives you both the dashboard and the morning job.
"""

import platform
import subprocess

from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger

from screener import cache
from screener.rank import run_screen

DEFAULT_HOUR = 7
DEFAULT_MINUTE = 30


def notify_macos(title: str, message: str) -> None:
    if platform.system() != "Darwin":
        return
    script = f'display notification "{message}" with title "{title}" sound name "Glass"'
    try:
        subprocess.run(["osascript", "-e", script], check=False, timeout=10)
    except Exception:
        pass


def _format_summary(result: dict) -> str:
    # Keep the notification short: first 5 of the top 20 shown on the dashboard.
    lines = [f"{s['ticker']} ({s['final_score']})" for s in result.get("top", [])[:5]]
    return ", ".join(lines) if lines else "No results"


def run_morning_scan_and_notify() -> None:
    result = run_screen()
    cache.save_latest(result)
    notify_macos("Morning Movers — Top 5", _format_summary(result))


def start_scheduler(hour: int = DEFAULT_HOUR, minute: int = DEFAULT_MINUTE) -> BackgroundScheduler:
    scheduler = BackgroundScheduler(daemon=True)
    scheduler.add_job(
        run_morning_scan_and_notify,
        trigger=CronTrigger(day_of_week="mon-fri", hour=hour, minute=minute),
        id="morning_scan",
        replace_existing=True,
    )
    scheduler.start()
    return scheduler
