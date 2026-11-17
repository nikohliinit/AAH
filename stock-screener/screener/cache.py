"""Simple JSON cache so the dashboard doesn't need to re-scan on every page load."""

import json
import os

CACHE_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "cache")
LATEST_PATH = os.path.join(CACHE_DIR, "latest.json")


def save_latest(result: dict) -> None:
    os.makedirs(CACHE_DIR, exist_ok=True)
    with open(LATEST_PATH, "w") as f:
        json.dump(result, f, indent=2)


def load_latest() -> dict | None:
    if not os.path.exists(LATEST_PATH):
        return None
    with open(LATEST_PATH) as f:
        return json.load(f)
