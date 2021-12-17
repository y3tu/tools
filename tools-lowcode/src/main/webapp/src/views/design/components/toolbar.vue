<template>
  <div>
    <div class="toolbar">
      <el-button>撤消</el-button>
      <el-button>重做</el-button>
      <el-button>预览</el-button>
      <el-button>保存</el-button>
      <el-button>清空画布</el-button>
      <el-button>组合</el-button>
      <el-button>拆分</el-button>
      <el-button>锁定</el-button>
      <el-button>解锁</el-button>
      <div class="canvas-config">
        <span>画布大小</span>
        <input/>
        <span>*</span>
        <input/>
      </div>
      <div class="canvas-config">
        <span>画布比例</span>
        <input> %
      </div>
    </div>
  </div>
</template>

<script setup>

</script>

<style lang="scss" scoped>
.toolbar {
  padding: 15px 10px;
  white-space: nowrap;
  overflow-x: auto;
  background: #fff;
  border-bottom: 1px solid #ddd;

  .canvas-config {
    display: inline-block;
    margin-left: 10px;
    font-size: 14px;
    color: #606266;

    input {
      width: 50px;
      margin-left: 10px;
      outline: none;
      padding: 0 5px;
      border: 1px solid #ddd;
      color: #606266;
    }

    span {
      margin-left: 10px;
    }
  }

  .insert {
    display: inline-block;
    line-height: 1;
    white-space: nowrap;
    cursor: pointer;
    background: #fff;
    border: 1px solid #dcdfe6;
    color: #606266;
    -webkit-appearance: none;
    text-align: center;
    box-sizing: border-box;
    outline: 0;
    margin: 0;
    transition: .1s;
    font-weight: 500;
    padding: 9px 15px;
    font-size: 12px;
    border-radius: 3px;
    margin-left: 10px;

    &:active {
      color: #3a8ee6;
      border-color: #3a8ee6;
      outline: 0;
    }

    &:hover {
      background-color: #ecf5ff;
      color: #3a8ee6;
    }
  }
}
</style>
