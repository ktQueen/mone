<!--
 * @Description: 
 * @Date: 2024-03-06 18:03:33
 * @LastEditTime: 2024-08-11 18:25:32
-->
<template>
  <div class="base-link">
    <el-link type="primary" :underline="false"  :disabled="props.disabled"
      ><i :class="'iconfont ' + props.icon"></i>{{ props.name }}</el-link
    >
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  name: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped lang="scss">
.base-link {
  padding-right: 16px;
  .iconfont {
    padding-right: 4px;
  }
}
</style>
