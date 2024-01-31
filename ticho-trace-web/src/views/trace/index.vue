<template>
  <div>
    <BasicTable @register="registerTable" @resize-column="handleResizeColumn">
      <template #bodyCell="{ column }">
        <!--suppress TypeScriptUnresolvedReference -->
        <template v-if="column.key === 'action'">
          <TableAction stopButtonPropagation />
        </template>
      </template>
    </BasicTable>
  </div>
</template>
<script lang="ts">
  import { defineComponent, ref } from 'vue';
  import { BasicTable, TableAction, useTable } from '/@/components/Table';
  import { getTableColumns, getSearchColumns } from './tableData';
  import { tracePage } from '/@/api/trace/trace';
  import { message } from 'ant-design-vue';

  export default defineComponent({
    components: {
      TableAction,
      BasicTable,
    },
    setup() {
      const checkedKeys = ref<Array<string | number>>([]);
      const checkedKeysCount = ref<string | number>();
      const [registerTable] = useTable({
        title: '',
        api: tracePageProxy,
        columns: getTableColumns(),
        useSearchForm: true,
        formConfig: getSearchColumns(),
        showTableSetting: true,
        tableSetting: { fullScreen: true },
        showIndexColumn: false,
        rowKey: 'id',
        rowSelection: {
          type: 'checkbox',
          onSelect: onSelect,
          onSelectAll: onSelectAll,
        },
        striped: true,
        canColDrag: true,
        pagination: {
          pageSize: 15,
          pageSizeOptions: ['10', '15', '20', '30', '50', '100'],
          position: ['bottomCenter'],
          size: 'large',
        },
        loading: true,
        bordered: true,
      });

      function tracePageProxy(params) {
        return tracePage(params).catch((err) => {
          message.error(err.message);
        });
      }

      function onSelect(record, selected) {
        if (selected) {
          checkedKeys.value = [...checkedKeys.value, record.id];
        } else {
          checkedKeys.value = checkedKeys.value.filter((id) => id !== record.id);
        }
        checkedKeysCount.value = checkedKeys.value.length > 0 ? checkedKeys.value.length : '';
      }

      function onSelectAll(selected, _selectedRows, changeRows) {
        const changeIds = changeRows.map((item) => item.id);
        if (selected) {
          checkedKeys.value = [...checkedKeys.value, ...changeIds];
        } else {
          checkedKeys.value = checkedKeys.value.filter((id) => {
            return !changeIds.includes(id);
          });
        }
        checkedKeysCount.value = checkedKeys.value.length > 0 ? checkedKeys.value.length : '';
      }
      function handleResizeColumn(w, col) {
        col.width = w;
      }
      return {
        registerTable,
        checkedKeys,
        checkedKeysCount,
        onSelect,
        onSelectAll,
        handleResizeColumn,
        tracePageProxy,
      };
    },
  });
</script>
