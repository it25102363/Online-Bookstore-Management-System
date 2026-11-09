
package com.WD30Final.BookStore_ManagementSystem.entity;

public enum OrderStatus {

    PENDING {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return next == CONFIRMED
                    || next == CANCELLED;
        }
    },

    CONFIRMED {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return next == SHIPPED
                    || next == CANCELLED;
        }
    },

    SHIPPED {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return next == DELIVERED;
        }
    },

    DELIVERED {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return false;
        }
    },

    CANCELLED {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return false;
        }
    };

    // Each status provides its own implementation.
    public abstract boolean canTransitionTo(OrderStatus next);
}
