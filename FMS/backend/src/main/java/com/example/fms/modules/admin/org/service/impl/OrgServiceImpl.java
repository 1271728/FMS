package com.example.fms.modules.admin.org.service.impl;

import com.example.fms.modules.admin.org.dto.OrgTreeNode;
import com.example.fms.modules.admin.org.mapper.OrgTreeMapper;
import com.example.fms.modules.admin.org.service.OrgService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrgServiceImpl implements OrgService {

    private final OrgTreeMapper orgTreeMapper;

    public OrgServiceImpl(OrgTreeMapper orgTreeMapper) {
        this.orgTreeMapper = orgTreeMapper;
    }

    @Override
    public List<OrgTreeNode> tree() {
        List<OrgTreeNode> flat = orgTreeMapper.selectAll();
        Map<Long, OrgTreeNode> map = new LinkedHashMap<>();
        for (OrgTreeNode n : flat) {
            n.setChildren(new ArrayList<OrgTreeNode>());
            map.put(n.getId(), n);
        }
        List<OrgTreeNode> roots = new ArrayList<>();
        for (OrgTreeNode n : flat) {
            if (n.getParentId() == null || n.getParentId() == 0L || !map.containsKey(n.getParentId())) {
                roots.add(n);
            } else {
                map.get(n.getParentId()).getChildren().add(n);
            }
        }
        return roots;
    }
}
