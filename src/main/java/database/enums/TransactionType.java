package database.enums;

/**
 * Represents the type of a transaction.
 * Provides custom string representations for transaction types.
 */
public enum TransactionType {
    WITHDRAW {
        /**
         * Returns the custom string representation for the WITHDRAW type.
         *
         * @return "withdraw".
         */
        @Override
        public String toString() {
            return "withdraw";
        }
    },
    DEPOSIT {
        /**
         * Returns the custom string representation for the DEPOSIT type.
         *
         * @return "deposit".
         */
        @Override
        public String toString() {
            return "deposit";
        }
    }
}
