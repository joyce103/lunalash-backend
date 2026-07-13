package com.example.lunalash.exception;

// 核准預約時，同一個日期+時段已經有另一筆被核准的預約 (race condition)
public class SlotConflictException extends RuntimeException {
    public SlotConflictException(String message) {
        super(message);
    }
}
