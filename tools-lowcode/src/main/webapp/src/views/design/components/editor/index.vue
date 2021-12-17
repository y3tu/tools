<template>
  <div id="editor"
       class="editor"
       @contextmenu="handleContextMenu"
       @mousedown="handleMouseDown">

    <!--页面组件列表展示-->

  </div>
</template>

<script setup>

const handleContextMenu = function () {

}

const handleMouseDown = function () {

}
</script>

<style lang="scss" scoped>
.editor {
  position: relative;
  background: #fff;
  margin: auto;

  .lock {
    opacity: .5;

    &:hover {
      cursor: not-allowed;
    }
  }
}

.edit {
  .component {
    outline: none;
    width: 100%;
    height: 100%;
  }
}
</style>
