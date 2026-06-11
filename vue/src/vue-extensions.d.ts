// 扩展 Vue 全局属性类型，解决 this.$store / this.$router 的 TS 报错
import type { Router } from 'vue-router';
import type { Store } from 'vuex';

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $router: Router;
    $route: Router['currentRoute']['value'];
    $store: Store<any>;
  }
}
