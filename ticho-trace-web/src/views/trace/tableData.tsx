import { FormProps } from '/@/components/Table';
import { BasicColumn } from '/@/components/Table/src/types/table';
import moment from 'moment';
import { useUserStore } from '/@/store/modules/user';

function getSystem() {
  const userStore = useUserStore();
  const { systems } = userStore?.getUserInfo;
  return systems.map((item) => {
    return {
      label: item.systemName,
      value: item.systemId,
    };
  });
}

function getFirstSystemId() {
  const userStore = useUserStore();
  const { systems } = userStore?.getUserInfo;
  return systems[0].systemId;
}

export function getSearchColumns(): Partial<FormProps> {
  return {
    labelWidth: 100,
    schemas: [
      {
        field: `systemId`,
        label: `系统`,
        component: 'Select',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请选择系统',
          style: {
            width: '100%',
          },
          options: getSystem(),
        },
        defaultValue: getFirstSystemId(),
      },
      {
        field: `traceId`,
        label: `链路id`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入链路id',
        },
      },
      {
        field: `spanId`,
        label: `跨度id`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入跨度id',
        },
      },
      {
        field: `appName`,
        label: `应用名称`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入应用名称',
        },
      },
      {
        field: `env`,
        label: `应用环境`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入应用环境',
        },
      },
      {
        field: `ip`,
        label: `ip`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入ip',
        },
      },
      {
        field: `preAppName`,
        label: `上个链路的应用名称`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入上个链路的应用名称',
        },
      },
      {
        field: `preIp`,
        label: `上个链路的Ip`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入上个链路的Ip',
        },
      },
      {
        field: `type`,
        label: `请求类型`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入请求类型',
        },
      },
      {
        field: `url`,
        label: `接口`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入接口',
        },
      },
      {
        field: `port`,
        label: `端口号`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入端口号',
        },
      },
      {
        field: `fullUrl`,
        label: `全路径接口`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入全路径接口',
        },
      },
      {
        field: `method`,
        label: `方法`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入方法',
        },
      },
      {
        field: `status`,
        label: `响应状态`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入响应状态',
        },
      },
      {
        field: `startTimeFirst`,
        label: `请求开始时间起始`,
        component: 'DatePicker',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入请求开始时间起始',
          // 传给后端的时间格式--
          valueFormat: 'YYYY-MM-DD HH:mm:ss',
          // 显示的时间格式
          showTime: {
            format: 'YYYY-MM-DD HH:mm:ss',
          },
          // 设置不可选
          disabledDate: (current) => {
            // 不可选择未来时间
            return current && current > Date.now();
          },
          style: {
            width: '100%',
          },
        },
        defaultValue: moment().add(-7, 'd').format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        field: `startTimeLast`,
        label: `请求开始时间终止`,
        component: 'DatePicker',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入请求开始时间终止',
          // 传给后端的时间格式--
          valueFormat: 'YYYY-MM-DD HH:mm:ss',
          // 显示的时间格式
          showTime: {
            format: 'YYYY-MM-DD HH:mm:ss',
          },
          // 设置不可选
          disabledDate: (current) => {
            // 不可选择未来时间
            return current && current > Date.now();
          },
          style: {
            width: '100%',
          },
        },
        defaultValue: moment().format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        field: `endTimeFirst`,
        label: `请求结束时间起始`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入请求结束时间起始',
        },
      },
      {
        field: `endTimeLast`,
        label: `请求结束时间终止`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入请求结束时间终止',
        },
      },
      {
        field: `consumeFirst`,
        label: `耗时起始`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入耗时起始',
        },
      },
      {
        field: `consumeLast`,
        label: `耗时终止`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入耗时终止',
        },
      },
    ],
  };
}

export function getTableColumns(): BasicColumn[] {
  return [
    {
      title: '系统名称',
      dataIndex: 'systemName',
      resizable: true,
      width: 60,
    },
    {
      title: '链路id',
      dataIndex: 'traceId',
      resizable: true,
      width: 100,
    },
    {
      title: '跨度id',
      dataIndex: 'spanId',
      resizable: true,
      width: 60,
    },
    {
      title: '应用名称',
      dataIndex: 'appName',
      resizable: true,
      width: 100,
    },
    {
      title: '应用环境',
      dataIndex: 'env',
      resizable: true,
      width: 60,
    },
    {
      title: '请求类型',
      dataIndex: 'type',
      resizable: true,
      width: 40,
    },
    {
      title: '接口',
      dataIndex: 'url',
      resizable: true,
      width: 100,
    },
    {
      title: 'ip',
      dataIndex: 'ip',
      resizable: true,
      width: 60,
    },
    {
      title: '端口号',
      dataIndex: 'port',
      resizable: true,
      width: 40,
    },
    {
      title: '上个链路的应用名称',
      dataIndex: 'preAppName',
      resizable: true,
      width: 100,
    },
    {
      title: '上个链路的Ip',
      dataIndex: 'preIp',
      resizable: true,
      width: 60,
    },
    {
      title: '方法',
      dataIndex: 'method',
      resizable: true,
      width: 40,
    },
    {
      title: '响应状态',
      dataIndex: 'status',
      resizable: true,
      width: 40,
    },
    {
      title: '请求开始时间',
      dataIndex: 'startTime',
      resizable: true,
      width: 160,
    },
    {
      title: '请求结束时间',
      dataIndex: 'endTime',
      resizable: true,
      width: 160,
    },
    {
      title: '耗时',
      dataIndex: 'consume',
      resizable: true,
      width: 60,
    },
  ];
}
