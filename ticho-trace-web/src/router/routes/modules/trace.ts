import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';

const setup: AppRouteModule = {
  path: '/trace',
  name: 'Trace',
  component: LAYOUT,
  redirect: '/trace/index',
  meta: {
    orderNo: 40,
    hideChildrenInMenu: true,
    icon: 'icon-park-outline:trace',
    title: '链路信息',
  },
  children: [
    {
      path: 'index',
      name: 'TracePage',
      component: () => import('/@/views/trace/index.vue'),
      meta: {
        title: '链路列表',
        icon: 'whh:paintroll',
        hideMenu: true,
      },
    },
  ],
};

export default setup;
