<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'

const props = defineProps<{
  modelValue: string
  placeholder?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const editorRef = ref<HTMLDivElement | null>(null)
const isFocused = ref(false)

// Sync modelValue to editor content once on mount, and when it changes externally
watch(
  () => props.modelValue,
  (newVal) => {
    if (editorRef.value && editorRef.value.innerHTML !== newVal) {
      editorRef.value.innerHTML = newVal || ''
    }
  }
)

onMounted(() => {
  if (editorRef.value) {
    editorRef.value.innerHTML = props.modelValue || ''
  }
})

// Emit the changes
const onInput = () => {
  if (editorRef.value) {
    emit('update:modelValue', editorRef.value.innerHTML)
  }
}

// Formatting commands helper
const format = (command: string, value: string = '') => {
  document.execCommand(command, false, value)
  onInput()
}
</script>

<template>
  <div class="rich-text-editor" :class="{ 'focused': isFocused }">
    <!-- Toolbar -->
    <div class="editor-toolbar">
      <button type="button" class="toolbar-btn" title="Bold" @click="format('bold')">
        <font-awesome-icon icon="fa-solid fa-bold" />
      </button>
      <button type="button" class="toolbar-btn" title="Italic" @click="format('italic')">
        <font-awesome-icon icon="fa-solid fa-italic" />
      </button>
      <button type="button" class="toolbar-btn" title="Underline" @click="format('underline')">
        <font-awesome-icon icon="fa-solid fa-underline" />
      </button>
      <button type="button" class="toolbar-btn" title="Strikethrough" @click="format('strikeThrough')">
        <font-awesome-icon icon="fa-solid fa-strikethrough" />
      </button>
      <span class="divider"></span>
      <button type="button" class="toolbar-btn" title="Heading 1" @click="format('formatBlock', '<h1>')">
        H1
      </button>
      <button type="button" class="toolbar-btn" title="Heading 2" @click="format('formatBlock', '<h2>')">
        H2
      </button>
      <button type="button" class="toolbar-btn" title="Paragraph" @click="format('formatBlock', '<p>')">
        P
      </button>
      <span class="divider"></span>
      <button type="button" class="toolbar-btn" title="Unordered List" @click="format('insertUnorderedList')">
        <font-awesome-icon icon="fa-solid fa-list-ul" />
      </button>
      <button type="button" class="toolbar-btn" title="Ordered List" @click="format('insertOrderedList')">
        <font-awesome-icon icon="fa-solid fa-list-ol" />
      </button>
      <span class="divider"></span>
      <button type="button" class="toolbar-btn" title="Align Left" @click="format('justifyLeft')">
        <font-awesome-icon icon="fa-solid fa-align-left" />
      </button>
      <button type="button" class="toolbar-btn" title="Align Center" @click="format('justifyCenter')">
        <font-awesome-icon icon="fa-solid fa-align-center" />
      </button>
      <button type="button" class="toolbar-btn" title="Align Right" @click="format('justifyRight')">
        <font-awesome-icon icon="fa-solid fa-align-right" />
      </button>
      <span class="divider"></span>
      <button type="button" class="toolbar-btn color-picker-wrapper" title="Text Color">
        <font-awesome-icon icon="fa-solid fa-palette" />
        <input type="color" class="color-picker" @input="(e: any) => format('foreColor', e.target.value)" />
      </button>
      <button type="button" class="toolbar-btn" title="Clear Format" @click="format('removeFormat')">
        <font-awesome-icon icon="fa-solid fa-eraser" />
      </button>
    </div>

    <!-- Editable Area -->
    <div
      ref="editorRef"
      class="editor-content"
      contenteditable="true"
      :placeholder="placeholder || 'Nhập nội dung...'"
      @input="onInput"
      @focus="isFocused = true"
      @blur="isFocused = false"
    ></div>
  </div>
</template>

<style scoped>
.rich-text-editor {
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  background: #ffffff;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.02);
}

.rich-text-editor.focused {
  border-color: #06b6d4;
  box-shadow: 0 0 12px rgba(6, 182, 212, 0.12);
}

.editor-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #f8fafc;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.toolbar-btn {
  background: transparent;
  border: none;
  color: #475569;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  min-width: 32px;
  height: 32px;
}

.toolbar-btn:hover {
  background: rgba(0, 0, 0, 0.04);
  color: #0f172a;
}

.divider {
  width: 1px;
  height: 20px;
  background: rgba(0, 0, 0, 0.08);
  margin: 0 4px;
}

.color-picker-wrapper {
  position: relative;
  overflow: hidden;
}

.color-picker {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.editor-content {
  padding: 16px;
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
  color: #0f172a;
  font-size: 15px;
  line-height: 1.6;
  outline: none;
  text-align: left;
}

.editor-content[contenteditable="true"]:empty:before {
  content: attr(placeholder);
  color: rgba(0, 0, 0, 0.4);
  pointer-events: none;
  display: block;
}

/* Custom styling for content within editor */
.editor-content :deep(h1) {
  font-size: 1.8rem;
  font-weight: 700;
  margin-top: 16px;
  margin-bottom: 8px;
  color: #0f172a;
}

.editor-content :deep(h2) {
  font-size: 1.4rem;
  font-weight: 600;
  margin-top: 14px;
  margin-bottom: 6px;
  color: #1e293b;
}

.editor-content :deep(p) {
  margin-bottom: 12px;
}

.editor-content :deep(ul) {
  list-style-type: disc;
  padding-left: 24px;
  margin-bottom: 12px;
}

.editor-content :deep(ol) {
  list-style-type: decimal;
  padding-left: 24px;
  margin-bottom: 12px;
}

.editor-content :deep(li) {
  margin-bottom: 4px;
}
</style>
