package paxos;

public enum MsgType {

    /**
     * CLIENT START THE PAXOS
     */
    SERVER,

    /**
     * the propose stage
     */
    PREPARE,

    /**
     * the accept stage
     */
    ACCEPT,

    /**
     * the learn stage
     */
    LEARN,

    /**
     * learn when online
     */
    START_LEARN;
}
