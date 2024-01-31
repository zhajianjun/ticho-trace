import { defHttp } from '/@/utils/http/axios';
import { Trace, TraceQuery } from '/@/api/trace/model/traceModel';

enum Api {
  GetByTraceId = '/trace/getByTraceId',
  TracePage = '/trace/page',
}

export function tracePage(params: TraceQuery) {
  return defHttp.get<Trace>({ url: Api.TracePage, params }, { errorMessageMode: 'none' });
}

export function getByTraceId(params: string) {
  return defHttp.get<Trace>(
    { url: Api.GetByTraceId, params },
    { successMessageMode: 'message', errorMessageMode: 'message' },
  );
}
