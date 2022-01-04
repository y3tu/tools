const modules = import.meta.globEager('./*/index.(tsx|vue)')

interface componentsType{
  [name:string]:any
}
const components :componentsType={}

for (const path in modules) {
  const comp = modules[path].default
  components[comp.name] = comp
}
export default components
