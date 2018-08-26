package com.n26.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.n26.pojo.Metric;
import com.n26.statistics.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticController {

    private Statistics statistics;

    @Autowired
    public StatisticController(Statistics statistics) {
        this.statistics = statistics;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity getStatistics() {
        Metric metric = statistics.getStatistics();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("sum", metric.getSum().toString());
        objectNode.put("avg", metric.getAvg().toString());
        objectNode.put("max", metric.getMax().toString());
        objectNode.put("min", metric.getMin().toString());
        objectNode.put("count", metric.getCount());
        return new ResponseEntity(objectNode, HttpStatus.OK);

    }
}
