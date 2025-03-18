<template>
    <div>
        <h1>{{ message }}</h1>
    </div>
</template>

<script>
export default {
    data() {
        return {
            message: ''
        };
    },
    created() {
        this.fetchHelloMessage();
    },
    methods: {
        async fetchHelloMessage() {
            const response = await fetch('http://localhost:8080/front/hello');
            const data = await response.text();
            this.message = data;
        }
    }
};
</script>

<style scoped>
h1 {
    color: #42b983;
}
</style>