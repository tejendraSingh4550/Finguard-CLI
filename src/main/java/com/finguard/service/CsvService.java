package com.finguard.service;

import org.apache.commons.csv.*;
import com.finguard.model.*;
import com.finguard.util.InputValidator;
import java.io.*;
import java.nio.file.*;
import java.math.BigDecimal;
import java.util.*;

public class CsvService {
    private static final CSVFormat INPUT = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build();
    private final TransactionService tx;
    public CsvService(TransactionService tx) { this.tx = tx; }

    public void export(Path path) throws Exception {
        try (var writer = Files.newBufferedWriter(path);
             var printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("account_id", "reference", "amount", "currency", "type", "merchant", "timestamp", "status").build())) {
            for (var t : tx.all()) printer.printRecord(t.accountId(), t.reference(), t.amount(), t.currency(), t.type(), t.merchant(), t.timestamp(), t.status());
        }
        tx.recordEvent("CSV_EXPORT", "Exported transactions to " + path.getFileName());
    }

    public String importFile(Path path) throws IOException {
        int imported = 0, rejected = 0, row = 1;
        var errors = new StringBuilder();
        try (var reader = Files.newBufferedReader(path); var parser = INPUT.parse(reader)) {
            var headers = parser.getHeaderMap();
            for (String name : List.of("account_id", "reference", "amount", "currency", "type", "merchant", "timestamp")) {
                if (!headers.containsKey(name)) return "Import rejected: missing required column '" + name + "'.";
            }
            for (var record : parser) {
                row++;
                try {
                    var status = headers.containsKey("status") ? Optional.ofNullable(record.get("status")).filter(s -> !s.isBlank()).orElse("COMPLETED") : "COMPLETED";
                    var transaction = new Transaction(null, record.get("account_id"), record.get("reference"),
                            new BigDecimal(record.get("amount")), record.get("currency"), InputValidator.type(record.get("type")),
                            record.get("merchant"), InputValidator.timestamp(record.get("timestamp")),
                            TransactionStatus.valueOf(status.toUpperCase(Locale.ROOT)), 0, RiskLevel.LOW, List.of());
                    tx.add(transaction);
                    imported++;
                } catch (Exception e) {
                    rejected++;
                    errors.append("Row ").append(row).append(": ").append(e.getMessage()).append('\n');
                }
            }
        } catch (IllegalArgumentException e) {
            return "Import rejected: malformed CSV: " + e.getMessage();
        }
        try { tx.recordEvent("CSV_IMPORT", "Imported " + imported + " rows; rejected " + rejected + " rows from " + path.getFileName()); }
        catch (Exception e) { errors.append("Audit logging failed: ").append(e.getMessage()); }
        return "Imported: " + imported + ", rejected: " + rejected + (rejected == 0 ? "" : "\n" + errors);
    }
}
