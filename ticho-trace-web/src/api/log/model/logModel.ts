export interface Log {
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
  /** 日志级别 */
  logLevel: string;
  /** 日志时间 */
  dateTime: string;
  /** 日志时间戳 */
  dtTime: number;
  /** 类名称 */
  className: string;
  /** 方法名 */
  method: string;
  /** 序列号 */
  seq: number;
  /** 内容 */
  content: string;
  /** 线程名称 */
  threadName: string;
  /** 创建时间 */
  createTime: string;
}

export interface LogQuery {
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
  /** 日志级别 */
  logLevel: string;
  /** 日志时间起 */
  startDateTime: string;
  /** 日志时间止 */
  endDateTime: string;
  /** 类名称 */
  className: string;
  /** 方法名 */
  method: string;
  /** 内容 */
  content: string;
  /** 是否按照时间递增排序 */
  isAsc: number;
}
