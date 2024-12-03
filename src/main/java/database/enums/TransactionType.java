package database.enums;

public enum TransactionType {
    WITHDRAW{
        @Override
        public String toString() {
            return "withdraw";
        }
    }, DEPOSIT{
        @Override
        public String toString(){
            return "deposit";
        }
    }
}
