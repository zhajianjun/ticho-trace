export interface Trace {
  /** 主键编号 */
  id: string;
  /** 系统id */
  systemId: string;
  /** 系统名称 */
  systemName: string;
  /** 链路id */
  traceId: string;
  /** 跨度id */
  spanId: string;
  /** 应用名称 */
  appName: string;
  /** 应用环境 */
  env: string;
  /** ip */
  ip: string;
  /** 上个链路的应用名称 */
  preAppName: string;
  /** 上个链路的Ip */
  preIp: string;
  /** 请求类型 */
  type: string;
  /** 接口 */
  url: string;
  /** 端口号 */
  port: string;
  /** 全路径接口 */
  fullUrl: string;
  /** 方法 */
  method: string;
  /** 响应状态 */
  status: number;
  /** 请求开始时间戳 */
  start: number;
  /** 请求结束时间戳 */
  end: number;
  /** 请求开始时间 */
  startTime: string;
  /** 请求结束时间 */
  endTime: string;
  /** 耗时 */
  consume: number;
  /** 创建时间 */
  createTime: string;
}

export interface TraceQuery {
  /** 主键编号 */
  id: number;
  /** 系统id */
  systemId: string;
  /** 链路id */
  traceId: string;
  /** 跨度id */
  spanId: string;
  /** 应用名称 */
  appName: string;
  /** 应用环境 */
  env: string;
  /** ip */
  ip: string;
  /** 上个链路的应用名称 */
  preAppName: string;
  /** 上个链路的Ip */
  preIp: string;
  /** 请求类型 */
  type: string;
  /** 接口 */
  url: string;
  /** 端口号 */
  port: string;
  /** 全路径接口 */
  fullUrl: string;
  /** 方法 */
  method: string;
  /** 响应状态 */
  status: number;
  /** 请求开始时间起始 */
  startTimeFirst: string;
  /** 请求开始时间终止 */
  startTimeLast: string;
  /** 请求结束时间起始 */
  endTimeFirst: string;
  /** 请求结束时间终止 */
  endTimeLast: string;
  /** 耗时起始 */
  consumeFirst: number;
  /** 耗时终止 */
  consumeLast: number;
}
