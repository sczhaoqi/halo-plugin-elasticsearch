import { definePlugin } from "@halo-dev/console-shared";
import ConfigSearchView from "./views/ConfigSearchView.vue";
import { IconPlug } from "@halo-dev/components";
import { markRaw } from "vue";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/configs",
        name: "ConfigS",
        component: ConfigSearchView,
        meta: {
          title: "配置搜索",
          searchable: true,
          menu: {
            name: "配置搜索",
            group: "配置",
            icon: markRaw(IconPlug),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
});
