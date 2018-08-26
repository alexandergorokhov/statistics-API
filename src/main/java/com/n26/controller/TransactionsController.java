package com.n26.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.n26.exception.InFutureTransactionException;
import com.n26.exception.OldTransactionException;
import com.n26.pojo.Transaction;
import com.n26.statistics.TransactionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@RestController
public class TransactionsController {

    private TransactionStatistics transactionStatistics;

    @Autowired
    public TransactionsController(TransactionStatistics transactionStatistics) {
        this.transactionStatistics = transactionStatistics;
    }


    @PostMapping("/transactions")
    @ResponseBody
    public ResponseEntity postTransactions(@RequestBody String transaction) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.readValue(transaction, ObjectNode.class);

            JsonNode jsonAmount = node.get("amount");
            JsonNode jsonTimestamp = node.get("timestamp");


            transactionJsonValidity(jsonAmount, jsonTimestamp);

            transactionStatistics.addTransaction(new Transaction(new BigDecimal(jsonAmount.asText()),
                    createTimeStamp(jsonTimestamp)), Instant.now().toEpochMilli());
        } catch (OldTransactionException ex) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (NullPointerException | JsonMappingException ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException | InFutureTransactionException | ParseException | DateTimeParseException ex) {
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }


    @DeleteMapping("/transactions")
    public ResponseEntity deleteTransactions() {
        transactionStatistics.reset();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private void transactionJsonValidity(JsonNode jsonAmount, JsonNode jsonTimestamp) {

        if (Objects.isNull(jsonAmount) || Objects.isNull(jsonTimestamp)) {
            throw new NullPointerException("Requested fields are not present or requiered fields has no data");
        }
    }

    private Timestamp createTimeStamp(JsonNode jsonTimestamp) throws ParseException {
        Instant inst = Instant.parse(jsonTimestamp.asText());
        return Timestamp.from(inst);

    }
}
