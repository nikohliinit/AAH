"""Pure price/volume technical indicators computed from a daily OHLCV history."""

import numpy as np
import pandas as pd


def rsi(close: pd.Series, period: int = 14) -> float:
    delta = close.diff()
    gain = delta.clip(lower=0)
    loss = -delta.clip(upper=0)
    avg_gain = gain.rolling(period).mean()
    avg_loss = loss.rolling(period).mean()
    rs = avg_gain / avg_loss.replace(0, np.nan)
    rsi_series = 100 - (100 / (1 + rs))
    return float(rsi_series.iloc[-1]) if not rsi_series.empty else 50.0


def macd_bullish(close: pd.Series) -> bool:
    ema12 = close.ewm(span=12, adjust=False).mean()
    ema26 = close.ewm(span=26, adjust=False).mean()
    macd_line = ema12 - ema26
    signal_line = macd_line.ewm(span=9, adjust=False).mean()
    return bool(macd_line.iloc[-1] > signal_line.iloc[-1] and macd_line.iloc[-2] <= signal_line.iloc[-2])


def golden_cross(close: pd.Series) -> bool:
    sma20 = close.rolling(20).mean()
    sma50 = close.rolling(50).mean()
    if len(close) < 51:
        return False
    return bool(sma20.iloc[-1] > sma50.iloc[-1])


def volume_spike_ratio(volume: pd.Series, window: int = 20) -> float:
    avg = volume.rolling(window).mean().iloc[-1]
    if not avg or np.isnan(avg) or avg == 0:
        return 1.0
    return float(volume.iloc[-1] / avg)


def momentum_pct(close: pd.Series, lookback: int = 10) -> float:
    if len(close) <= lookback:
        return 0.0
    return float((close.iloc[-1] / close.iloc[-lookback - 1] - 1) * 100)


def projected_trend(close: pd.Series, lookback: int = 20, horizon: int = 5) -> dict:
    """Naive linear-regression extrapolation of the last `lookback` closes,
    projected `horizon` trading days forward.

    This is a straight-line fit through recent prices, not a forecast model —
    real prices don't move in straight lines. R-squared (how well the line
    actually fits) is reported as a rough, honest confidence signal: a low R^2
    means the "trend" is mostly noise.
    """
    window = close.tail(lookback)
    if len(window) < max(5, lookback // 2):
        return {
            "direction": "flat",
            "projected_pct": 0.0,
            "projected_price": float(close.iloc[-1]),
            "r_squared": 0.0,
            "confidence": "low",
        }

    x = np.arange(len(window))
    y = window.values
    slope, intercept = np.polyfit(x, y, 1)

    y_pred = slope * x + intercept
    ss_res = np.sum((y - y_pred) ** 2)
    ss_tot = np.sum((y - y.mean()) ** 2)
    r_squared = float(1 - ss_res / ss_tot) if ss_tot > 0 else 0.0
    r_squared = max(0.0, r_squared)

    last_close = float(close.iloc[-1])
    projected_price = float(slope * (len(window) - 1 + horizon) + intercept)
    projected_pct = (projected_price / last_close - 1) * 100 if last_close else 0.0

    if projected_pct > 0.5:
        direction = "up"
    elif projected_pct < -0.5:
        direction = "down"
    else:
        direction = "flat"

    if r_squared >= 0.7:
        confidence = "high"
    elif r_squared >= 0.4:
        confidence = "medium"
    else:
        confidence = "low"

    return {
        "direction": direction,
        "projected_pct": round(projected_pct, 2),
        "projected_price": round(projected_price, 2),
        "r_squared": round(r_squared, 2),
        "confidence": confidence,
    }


def technical_score(history: pd.DataFrame) -> dict:
    """Score 0-100: blends RSI positioning, trend, MACD cross, volume, momentum."""
    close = history["Close"]
    volume = history["Volume"]

    rsi_val = rsi(close)
    # Sweet spot for "room to run": RSI 45-65 scores highest, extremes score low.
    if 45 <= rsi_val <= 65:
        rsi_component = 100
    elif rsi_val < 45:
        rsi_component = max(0, (rsi_val / 45) * 100)
    else:
        rsi_component = max(0, 100 - (rsi_val - 65) * (100 / 35))

    trend_component = 100 if golden_cross(close) else 40
    macd_component = 100 if macd_bullish(close) else 30

    vol_ratio = volume_spike_ratio(volume)
    volume_component = min(100, vol_ratio * 50)

    mom = momentum_pct(close)
    momentum_component = max(0, min(100, 50 + mom * 5))

    trend = projected_trend(close)

    score = (
        0.25 * rsi_component
        + 0.25 * trend_component
        + 0.20 * macd_component
        + 0.15 * volume_component
        + 0.15 * momentum_component
    )

    return {
        "score": round(score, 1),
        "rsi": round(rsi_val, 1),
        "golden_cross": trend_component == 100,
        "macd_bullish_cross": macd_component == 100,
        "volume_ratio": round(vol_ratio, 2),
        "momentum_10d_pct": round(mom, 2),
        "trend": trend,
    }
