import { http } from "./http";

export interface OrgTreeNode {
  id: number;
  parentId?: number | null;
  code?: string;
  name: string;
  enabled?: number;
  sortNo?: number;
  children?: OrgTreeNode[];
}

export function apiOrgTree() {
  return http.get<any, OrgTreeNode[]>("/org/tree");
}
