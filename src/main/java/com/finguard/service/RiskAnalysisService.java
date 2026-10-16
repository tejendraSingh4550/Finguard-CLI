package com.finguard.service;
import com.finguard.config.AppConfig; import com.finguard.model.*; import java.math.*; import java.time.*; import java.util.*;
public class RiskAnalysisService {
 private final AppConfig c; public RiskAnalysisService(AppConfig c){this.c=c;}
 public record Context(List<Transaction> accountTransactions){}
 public Transaction evaluate(Transaction t,List<Transaction> history){int score=0;var reasons=new ArrayList<String>();if(t.amount().compareTo(c.highValue())>0)add(reasons,"High-value transaction exceeds "+c.highValue(),30);
 var window=history.stream().filter(x->x.accountId().equals(t.accountId())&&!x.timestamp().isAfter(t.timestamp())&&!x.timestamp().isBefore(t.timestamp().minusMinutes(c.windowMinutes()))).toList();if(window.size()+1>=c.rapidCount())add(reasons,"Rapid activity: "+(window.size()+1)+" transactions within "+c.windowMinutes()+" minutes",25);
 if(window.stream().anyMatch(x->x.amount().compareTo(t.amount())==0))add(reasons,"Repeated transaction amount within short period",20);
 var day=history.stream().filter(x->x.accountId().equals(t.accountId())&&x.timestamp().toLocalDate().equals(t.timestamp().toLocalDate())).map(Transaction::amount).reduce(BigDecimal.ZERO,BigDecimal::add).add(t.amount());if(day.compareTo(c.dailyLimit())>0)add(reasons,"Daily transaction total exceeds "+c.dailyLimit(),35);
 if(t.amount().compareTo(c.highValue().multiply(new BigDecimal("2")))>0||window.size()>=c.rapidCount()+1)add(reasons,"Unusual demo activity pattern",15);
 score=Math.min(100,reasons.stream().mapToInt(r->r.startsWith("High-value")?30:r.startsWith("Rapid")?25:r.startsWith("Repeated")?20:r.startsWith("Daily")?35:15).sum());RiskLevel level=score>=c.highScore()?RiskLevel.HIGH:score>=c.mediumScore()?RiskLevel.MEDIUM:RiskLevel.LOW;return new Transaction(t.id(),t.accountId(),t.reference(),t.amount(),t.currency(),t.type(),t.merchant(),t.timestamp(),t.status(),score,level,reasons);
 }
 private void add(List<String> r,String reason,int ignored){r.add(reason);}
}
