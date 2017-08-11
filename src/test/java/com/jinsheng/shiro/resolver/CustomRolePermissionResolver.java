package com.jinsheng.shiro.resolver;

import com.google.common.collect.Lists;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.util.Collection;
import java.util.List;

/**
 * @author jinsheng
 */
public class CustomRolePermissionResolver implements RolePermissionResolver {
    @Override
    public Collection<Permission> resolvePermissionsInRole(String roleString) {
        List<Permission> permissionList = Lists.newArrayList();
        permissionList.add(new WildcardPermission("resource1"));
        permissionList.add(new WildcardPermission("resource2"));
        permissionList.add(new WildcardPermission("resource3"));
        return permissionList;
    }
}
