import { defHttp } from '/@/utils/http/axios';
import { System, SystemQuery } from '/@/api/system/model/system';

enum Api {
  System = '/system',
  SystemPage = '/system/page',
  SystemListAll = '/system/listAll',
}

export function systemPage(params: SystemQuery) {
  return defHttp.get<System>({ url: Api.SystemPage, params }, { errorMessageMode: 'none' });
}

export function systemlistAll() {
  return defHttp.get<System[]>({ url: Api.SystemListAll }, { errorMessageMode: 'none' });
}
export function saveySystem(params: any) {
  return defHttp.post<System>(
    { url: Api.System, params },
    { successMessageMode: 'message', errorMessageMode: 'message' },
  );
}

export function delSystem(params: string) {
  return defHttp.delete<System>(
    { url: Api.System + '?id=', params },
    { successMessageMode: 'message', errorMessageMode: 'message' },
  );
}

export function modifySystem(params: any) {
  return defHttp.put<System>(
    { url: Api.System, params },
    { successMessageMode: 'message', errorMessageMode: 'message' },
  );
}
