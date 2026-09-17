package com.erp.management.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/receipts")
@RequiredArgsConstructor
public class InventoryReceiptController {
    private final InventoryReceiptRepository receipts;

    @GetMapping
    public List<InventoryReceipt> all() {
        return receipts.findAllByOrderByReceivedAtDesc();
    }
}
