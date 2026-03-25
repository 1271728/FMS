package com.example.fms.modules.shared.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class UserSupportTest {

    private final UserSupport userSupport = new UserSupport(null, null, null);

    @Test
    void normalizeRoleCodes_shouldTrimUpperAndDeduplicate() {
        List<String> normalized = userSupport.normalizeRoleCodes(Arrays.asList(" admin ", "PI", "pi", null, ""));
        Assertions.assertEquals(Arrays.asList("ADMIN", "PI"), normalized);
    }

    @Test
    void accessCodes_shouldReturnExpectedEntriesForAdmin() {
        List<String> codes = userSupport.accessCodes(Arrays.asList("admin"));
        Assertions.assertTrue(codes.contains("HOME"));
        Assertions.assertTrue(codes.contains("PROJECT_MANAGE"));
        Assertions.assertTrue(codes.contains("WORKFLOW_CENTER"));
        Assertions.assertTrue(codes.contains("ADMIN_USERS"));
    }

    @Test
    void accessCodes_shouldReturnTeacherScopeWithoutAdminPages() {
        List<String> codes = userSupport.accessCodes(Arrays.asList("pi"));
        Assertions.assertTrue(codes.contains("HOME"));
        Assertions.assertTrue(codes.contains("PROJECT_MANAGE"));
        Assertions.assertFalse(codes.contains("ADMIN_USERS"));
        Assertions.assertFalse(codes.contains("WORKFLOW_CENTER"));
    }
}
