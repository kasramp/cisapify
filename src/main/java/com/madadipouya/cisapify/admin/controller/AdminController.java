package com.madadipouya.cisapify.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminController {

    @GetMapping(value = "/admin/accounts/{accountId}", produces = "application/json")
    public ResponseEntity<Map<String, String>> getAccount(@PathVariable int accountId) {
        return ResponseEntity.ok(Map.of("accountId", Integer.toString(accountId)));
    }
}