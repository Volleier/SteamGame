declare module '*.vue' {
  import type { DefineComponent } from 'vue';
  const component: DefineComponent<object, object, unknown>;
  export default component;
}

// 扩展 Vue 全局属性类型
import type { Router } from 'vue-router';
import type { Store } from 'vuex';

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $router: Router;
    $route: Router['currentRoute']['value'];
    $store: Store<any>;
  }
}
