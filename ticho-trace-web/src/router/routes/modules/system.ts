import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';

const setup: AppRouteModule = {
  path: '/system',
  name: 'System',
  component: LAYOUT,
  redirect: '/system/index',
  meta: {
    orderNo: 20,
    hideChildrenInMenu: true,
    icon: 'carbon:cics-system-group',
    title: '系统信息',
  },
  children: [
    {
      path: 'index',
      name: 'SystemPage',
      component: () => import('/@/views/system/index.vue'),
      meta: {
        title: '系统列表',
        icon: 'whh:paintroll',
        hideMenu: true,
      },
    },
  ],
};

export default setup;
