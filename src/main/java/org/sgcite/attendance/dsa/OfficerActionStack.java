package org.sgcite.attendance.dsa;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LIFO stack for logging officer actions and audit history.
 * The newest action is always positioned at the top of the stack.
 */
public class OfficerActionStack {
    private final Deque<String> actionStack = new ArrayDeque<>();

    public void pushAction(String actionLog) {
        if (actionLog != null) {
            actionStack.push(actionLog.trim());
        }
    }

    public String popLastAction() {
        return actionStack.isEmpty() ? "No recent actions." : actionStack.pop();
    }

    public void displayAuditHistory() {
        if (actionStack.isEmpty()) {
            System.out.println("No recent actions.");
            return;
        }

        System.out.println("=== OFFICER AUDIT HISTORY ===");
        for (String action : actionStack) {
            System.out.println(action);
        }
    }
}
