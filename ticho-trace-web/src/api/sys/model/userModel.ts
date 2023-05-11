import { System } from '/@/api/system/model/system';

export interface User {
  id: string;
  username: string;
  realname: string;
  email: string;
  mobile: string;
  photo: string;
  lastIp: string;
  lastTime: string;
  status: number;
  remark: string;
  createBy: string;
  createTime: string;
  updateBy: string;
  updateTime: string;
  systemIds: string[];
  systems: System[];
  avatar: string;
}

export interface UserQuery {
  pageNum: number;
  pageSize: number;
  id: string;
  username: string;
  realname: string;
  email: string;
  mobile: string;
  photo: string;
  lastIp: string;
  lastTime: string;
  status: number;
  remark: string;
  systemIds: string[];
}

