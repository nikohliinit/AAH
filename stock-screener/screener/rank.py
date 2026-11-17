"""Combine technical, analyst, and sentiment scores into a ranked top-N list.

This is a quantitative screener, not personalized financial advice. Output
should always be treated as a starting point for your own research.
"""

import datetime as dt

from . import indicators, analyst as analyst_mod, sentiment as sentiment_mod
from .data import fetch_universe
from .universe import TICKERS

WEIGHTS = {"technical": 0.5, "analyst": 0.3, "sentiment": 0.2}


def _build_reasons(tech: dict, an: dict, sent: dict) -> list[str]:
    reasons = []
    if tech["golden_cross"]:
        reasons.append("20-day average above 50-day average (uptrend)")
    if tech["macd_bullish_cross"]:
        reasons.append("MACD just crossed bullish")
    if tech["momentum_10d_pct"] > 2:
        reasons.append(f"Up {tech['momentum_10d_pct']}% over the last 10 sessions")
    if tech["volume_ratio"] > 1.3:
        reasons.append(f"Volume {tech['volume_ratio']}x the 20-day average")
    if 45 <= tech["rsi"] <= 65:
        reasons.append(f"RSI at {tech['rsi']} (room to run, not overbought)")
    if an.get("upside_pct") is not None and an["upside_pct"] > 5:
        reasons.append(f"Analyst target implies {an['upside_pct']}% upside")
    if an.get("recommendation") in ("buy", "strong_buy", "strongbuy"):
        reasons.append(f"Analyst consensus: {an['recommendation']}")
    if sent["headlines_analyzed"] > 0 and sent["score"] > 60:
        reasons.append("Recent news headlines skew positive")

    trend = tech.get("trend", {})
    if trend.get("direction") == "up" and trend.get("confidence") in ("medium", "high"):
        reasons.append(
            f"20-day trend line projects +{trend['projected_pct']}% over 5 sessions "
            f"({trend['confidence']} confidence)"
        )
    return reasons or ["Signals are mixed; scored on balance across factors"]


def run_screen(tickers: list[str] | None = None, top_n: int = 20) -> dict:
    tickers = tickers or TICKERS
    bundles = fetch_universe(tickers)

    results = []
    for bundle in bundles:
        try:
            tech = indicators.technical_score(bundle["history"])
            an = analyst_mod.analyst_score(bundle["info"])
            sent = sentiment_mod.sentiment_score(bundle["news"])

            final_score = (
                WEIGHTS["technical"] * tech["score"]
                + WEIGHTS["analyst"] * an["score"]
                + WEIGHTS["sentiment"] * sent["score"]
            )

            info = bundle["info"]
            last_close = float(bundle["history"]["Close"].iloc[-1])

            results.append({
                "ticker": bundle["ticker"],
                "name": info.get("shortName") or bundle["ticker"],
                "final_score": round(final_score, 1),
                "price": round(last_close, 2),
                "technical": tech,
                "analyst": an,
                "sentiment": sent,
                "reasons": _build_reasons(tech, an, sent),
            })
        except Exception:
            continue

    results.sort(key=lambda r: r["final_score"], reverse=True)
    top = results[:top_n]

    return {
        "generated_at": dt.datetime.now().isoformat(timespec="seconds"),
        "universe_size": len(tickers),
        "scanned_successfully": len(results),
        "top": top,
    }
