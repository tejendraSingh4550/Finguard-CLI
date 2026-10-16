package com.finguard.config;
import java.math.BigDecimal;
public record AppConfig(BigDecimal highValue,BigDecimal dailyLimit,int rapidCount,int windowMinutes,int mediumScore,int highScore) {
 public static AppConfig defaults(){return new AppConfig(new BigDecimal("5000"),new BigDecimal("10000"),3,10,30,60);}
 public static AppConfig load(){var d=defaults();return new AppConfig(new BigDecimal(System.getenv().getOrDefault("FG_HIGH_VALUE",d.highValue.toString())),new BigDecimal(System.getenv().getOrDefault("FG_DAILY_LIMIT",d.dailyLimit.toString())),Integer.parseInt(System.getenv().getOrDefault("FG_RAPID_COUNT",Integer.toString(d.rapidCount))),Integer.parseInt(System.getenv().getOrDefault("FG_WINDOW_MINUTES",Integer.toString(d.windowMinutes))),Integer.parseInt(System.getenv().getOrDefault("FG_MEDIUM_SCORE",Integer.toString(d.mediumScore))),Integer.parseInt(System.getenv().getOrDefault("FG_HIGH_SCORE",Integer.toString(d.highScore))));}
}
