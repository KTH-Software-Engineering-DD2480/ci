<template>
  <div class="hello">
    <table class="table">
      <tr>
        <th>Build name</th>
        <th>Build Status</th>
        <th>Url</th>
      </tr>
      <tr v-for="ci in cis" v-bind:key="ci">
        <td>Build Name {{ ci }}</td>
        <td>Build Status</td>
        <td>Url</td>
      </tr>
    </table>
  </div>
</template>

<script>
export default {
  name: "HelloWorld",
  props: {
    msg: String,
  },
  data() {
    return {
      cis: ["1", "2"],
    };
  },
};
</script>

<style scoped>
.hello {
  display: flex;
  flex-direction: column;
}

.table {
  width: 70vw;
  background: #dddddd;
}

table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

td,th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}
</style>
