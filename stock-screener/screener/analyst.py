"""Analyst-target based scoring, sourced from yfinance's free `info` payload."""

RECOMMENDATION_WEIGHTS = {
    "strong_buy": 100,
    "strongbuy": 100,
    "buy": 80,
    "hold": 50,
    "underperform": 20,
    "sell": 0,
}


def analyst_score(info: dict) -> dict:
    current = info.get("currentPrice") or info.get("regularMarketPrice")
    target = info.get("targetMeanPrice")
    rec_key = (info.get("recommendationKey") or "hold").lower()

    upside_pct = None
    upside_component = 50.0
    if current and target and current > 0:
        upside_pct = (target - current) / current * 100
        # Cap scoring contribution at +/-30% upside/downside.
        upside_component = max(0.0, min(100.0, 50 + upside_pct * (50 / 30)))

    rec_component = RECOMMENDATION_WEIGHTS.get(rec_key, 50)

    score = 0.6 * upside_component + 0.4 * rec_component

    return {
        "score": round(score, 1),
        "target_mean_price": target,
        "current_price": current,
        "upside_pct": round(upside_pct, 2) if upside_pct is not None else None,
        "recommendation": rec_key,
    }
