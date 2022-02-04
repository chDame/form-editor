const vueSetPath=function(prop, val) {
	let path = prop.split('.');
	let obj = store;
	while(path.length>1) {
		obj = obj[path[0]];
		path.splice(0,1);
	}
	Vue.set(obj, path[0], val);
}
const vueGetPath=function(prop) {
	let path = prop.split('.');
	let obj = store;
	while(path.length>1) {
		obj = obj[path[0]];
		path.splice(0,1);
	}
	return obj[path[0]];
}
Vue.component('input-element', {
  template: "<div><div class='mb-3' v-if='!props.icon'><label :for='props.id' class='form-label'>{{props.label}}</label><input :id='props.id' :type='props.type' class='form-control' :placeholder='props.placeholder' v-model='valueAccessor'></input></div>"+
  "<label v-if='props.icon' :for='props.id' class='form-label'>{{props.label}}</label><div v-if='props.icon' class='input-group mb-3'><span class='input-group-text'><i :class='props.icon'/></span><input :type='props.type' class='form-control' :id='props.id' :placeholder='props.placeholder' v-model='valueAccessor'></div></div>",
  props: ['props'],
  computed: {
    valueAccessor: {
      get() {
        return vueGetPath(this.props.value);
      },
      set(val) {
		vueSetPath(this.props.value,val);
      }
    }
  }
})
Vue.component('button-element', {
  template: '<button :class="[props.outlined ? \'btn btn-outline-\'+props.style: \'btn btn-\'+props.style]"><i :class="props.icon" v-if="props.icon"></i> {{ props.label }}</button>',
  props: ['props']
})

Vue.component('checkbox-element', {
  template: '<div class="form-check mb-1"><input class="form-check-input" type="checkbox"><label class="form-check-label">{{props.label}}</label></div>',
  props: ['props']
})

Vue.component('panel-element', {
  template: '<div :class="\'card \'+props.class">'+
  '<div class="card-header">{{props.title}}</div>'+
  '<div class="card-body"><slot></slot></div>'+
'</div>',
  props: ['props']
})

Vue.component('row-element', {
  template: '<div class="row"><slot></slot></div>',
  props: ['props']
})