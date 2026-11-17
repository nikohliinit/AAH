"""Concurrent data fetching from yfinance (free, no API key required)."""

import concurrent.futures as cf
import logging

import yfinance as yf

logging.getLogger("yfinance").setLevel(logging.CRITICAL)


def fetch_ticker_bundle(ticker: str) -> dict | None:
    """Fetch history + info + news for one ticker. Returns None on failure."""
    try:
        tk = yf.Ticker(ticker)
        history = tk.history(period="4mo", interval="1d", auto_adjust=True)
        if history is None or history.empty or len(history) < 30:
            return None

        info = {}
        try:
            info = tk.get_info()
        except Exception:
            info = {}

        news = []
        try:
            news = tk.news or []
        except Exception:
            news = []

        return {
            "ticker": ticker,
            "history": history,
            "info": info,
            "news": news,
        }
    except Exception:
        return None


def fetch_universe(tickers: list[str], max_workers: int = 10) -> list[dict]:
    bundles = []
    with cf.ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = {executor.submit(fetch_ticker_bundle, t): t for t in tickers}
        for future in cf.as_completed(futures):
            result = future.result()
            if result is not None:
                bundles.append(result)
    return bundles
