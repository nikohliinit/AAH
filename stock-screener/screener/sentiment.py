"""Lightweight keyword-based sentiment scoring over recent free news headlines.

This is a heuristic, not NLP-grade sentiment analysis: it counts positive vs
negative words in headline text. Good enough as one signal among several in a
quantitative screener, not a substitute for reading the news yourself.
"""

import re

POSITIVE_WORDS = {
    "beat", "beats", "surge", "surges", "soar", "soars", "rally", "rallies",
    "upgrade", "upgraded", "outperform", "record", "growth", "strong",
    "gain", "gains", "jump", "jumps", "bullish", "buy", "raises", "raised",
    "profit", "profits", "expansion", "breakthrough", "high", "wins", "win",
    "optimis", "positive", "boost", "boosts", "exceeds", "top", "tops",
}

NEGATIVE_WORDS = {
    "miss", "misses", "plunge", "plunges", "slump", "slumps", "downgrade",
    "downgraded", "underperform", "weak", "loss", "losses", "fall", "falls",
    "drop", "drops", "bearish", "sell", "cuts", "cut", "lawsuit",
    "investigation", "recall", "warning", "layoff", "layoffs", "decline",
    "declines", "concerns", "risk", "risks", "delay", "delays", "probe",
}


def _score_headline(text: str) -> int:
    words = set(re.findall(r"[a-z]+", text.lower()))
    return len(words & POSITIVE_WORDS) - len(words & NEGATIVE_WORDS)


def sentiment_score(news_items: list) -> dict:
    if not news_items:
        return {"score": 50.0, "headlines_analyzed": 0, "sample_headlines": []}

    raw_scores = []
    samples = []
    for item in news_items[:10]:
        title = item.get("title") or item.get("content", {}).get("title", "")
        if not title:
            continue
        raw_scores.append(_score_headline(title))
        samples.append(title)

    if not raw_scores:
        return {"score": 50.0, "headlines_analyzed": 0, "sample_headlines": []}

    avg_raw = sum(raw_scores) / len(raw_scores)
    # Map roughly [-3, 3] raw average onto [0, 100].
    score = max(0.0, min(100.0, 50 + avg_raw * (50 / 3)))

    return {
        "score": round(score, 1),
        "headlines_analyzed": len(raw_scores),
        "sample_headlines": samples[:3],
    }
