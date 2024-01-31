export interface System {
  id: string;
  systemId: string;
  systemName: string;
  secret: string;
  status: number;
  remark: string;
  createBy: string;
  createTime: string;
  updateBy: string;
  updateTime: string;
}

export interface SystemQuery {
  id: string;
  systemId: string;
  systemName: string;
  secret: string;
  status: number;
  remark: string;
}
