<template>
  <div class="plugin-wrap">
    <el-form ref="formRef" status-icon :model="state.search" :size="formSize">
      <el-row :gutter="20">
        <el-col :span="5">
          <el-form-item :label="`${t('plugin.pluginName')}:`">
            <el-input v-model="state.search.name" :placeholder="t('plugin.enterPluginName')" />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item :label="`${t('plugin.username')}:`">
            <el-input v-model="state.search.userName" :placeholder="t('plugin.enterUserName')" />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item props="category" label="类型:">
            <el-select
              v-model="state.search.categoryId"
              style="width: 100%"
              :placeholder="t('probot.userManager.selectType')"
              @change="handleSearch"
              clearable
            >
              <el-option
                v-for="item in probotStore.categoryList['2']"
                :key="item?.id"
                :label="item?.name"
                :value="item?.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item props="official" label="官方:">
            <el-select
              v-model="state.search.official"
              style="width: 100%"
              placeholder=""
              @change="handleSearch"
              clearable
            >
              <el-option
                v-for="item in officialOption"
                :key="item?.id"
                :label="item?.name"
                :value="item?.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="getList(1)">{{ t('common.search') }}</el-button>
        </el-col>
      </el-row>
    </el-form>
    <BaseList
      type="plugin"
      :loading="state.loading"
      :data="state.tableList"
      @onJump="handleJump"
    ></BaseList>
    <div class="pager" v-if="state.total">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="state.total"
        :page-size="state.search.pageSize"
        :v-model:current-page="state.search.pageNum"
        hide-on-single-page
        @change="handleChangePage"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, watch, ref } from 'vue'
import { pluginsList4home } from '@/api/plugins'
import BaseList from '@/components/probot/BaseList.vue'
import { t } from '@/locales'
import { useRoute, useRouter } from 'vue-router'
import { useProbotStore } from '@/stores/probot'

const route = useRoute()
const probotStore = useProbotStore()

const formSize = ref('large') //default\large
const router = useRouter()
const state = reactive({
  tableList: [],
  search: {
    name: undefined,
    categoryName: undefined,
    categoryId: undefined,
    userName: undefined,
    type: undefined,
    pageSize: 12,
    pageNum: 1,
    orgOnly: false,
    official: ''
  },
  total: 0,
  loading: false
})
const officialOption = [
  {
    id: 0,
    name: '非官方'
  },
  {
    id: 1,
    name: '官方'
  }
]

const handleJump = (item) => {
  router.push({
    path: `/plugin-detail`,
    name: 'AI Probot Plugin Detail',
    query: {
      id: item.id
    }
  })
}

const getList = (page = 1) => {
  state.loading = true
  state.search.pageNum = page
  pluginsList4home(state.search)
    .then(({ data }) => {
      if (data?.records?.length) {
        state.tableList = data.records.map((v: any) => ({
          ...v,
          userName: v.creator,
          name: v.pluginOrgName,
          desc: v.pluginOrgDesc
        }))
        state.total = data.totalRow
      } else {
        state.tableList = []
        state.total = 0
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      state.loading = false
    })
}

const handleChangePage = (page: number) => {
  getList(page)
}

const handleSearch = () => {
  getList(1)
}

watch(
  () => route.query.category,
  (category) => {
    if (route.name === 'AI Probot Plugin List') {
      state.search.categoryId = category ? Number(category) : undefined
      getList()
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped>
.plugin-wrap {
  padding: 20px 45px;
  .pager {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>
