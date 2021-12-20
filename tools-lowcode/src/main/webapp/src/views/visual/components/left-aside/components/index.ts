const modules = import.meta.globEager('./*/index.(tsx|vue)')

const components = {}

for (const path in modules) {
    const comp = modules[path].default
    components[comp.name || path.split('/')[1]] = comp
}
console.log('left-aside components:', components)
export default components
