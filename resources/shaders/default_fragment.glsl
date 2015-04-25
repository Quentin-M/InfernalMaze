#version 330 core

struct Light {
  vec4 position;
  vec4 diffuse;
  vec4 specular;
  float constantAttenuation, linearAttenuation, quadraticAttenuation;
  float spotCutoff, spotExponent;
  vec3 spotDirection;
};

struct Material {
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float shininess;
};

//////////////////////////////////////

in vec2 varying_textureCoord;
in vec4 varying_position; // position of the vertex (and fragment) in world space
in vec3 varying_normal; // surface normal vector in world space

out vec4 fragColor;

uniform sampler2D texImage;
uniform mat4 view_inv;
uniform vec3 ambient;
uniform Material material;
uniform int lightsCount;
uniform Light lights[16];

//////////////////////////////////////

void main() {
	vec3 normalDirection = normalize(varying_normal);
	vec3 viewDirection = normalize(vec3(view_inv * vec4(0.0, 0.0, 0.0, 1.0) - varying_position));
	vec3 lightDirection;
  	float attenuation;
  	
	// initialize total lighting with ambient lighting
  	vec3 totalLighting = vec3(ambient) * vec3(material.ambient);
  
	for(int index = 0; index < lightsCount; index++) { // for all light sources
		if (0.0 == lights[index].position.w) { // directional light?
			attenuation = 1.0; // no attenuation
			lightDirection = normalize(vec3(lights[index].position));
		} else { // point light or spotlight (or other kind of light) 
			vec3 positionToLightSource = vec3(lights[index].position - varying_position);
			float distance = length(positionToLightSource);
			lightDirection = normalize(positionToLightSource);
			
			attenuation = 1.0 / (lights[index].constantAttenuation + (lights[index].linearAttenuation * distance) + (lights[index].quadraticAttenuation * distance * distance));
						
			if (lights[index].spotCutoff <= 90.0) { // spotlight?
				float clampedCosine = max(0.0, dot(-lightDirection, normalize(lights[index].spotDirection)));
				if (clampedCosine < cos(radians(lights[index].spotCutoff))) { // outside of spotlight cone?
					attenuation = 0.0;
				} else {
					attenuation = attenuation * pow(clampedCosine, lights[index].spotExponent);   
				}
			}
		}
		
		vec3 diffuseReflection = attenuation * vec3(lights[index].diffuse) * vec3(material.diffuse) * max(0.0, dot(normalDirection, lightDirection));
		
		vec3 specularReflection;
		if (dot(normalDirection, lightDirection) < 0.0) { // light source on the wrong side?
			specularReflection = vec3(0.0, 0.0, 0.0); // no specular reflection
		} else { // light source on the right side
			specularReflection = attenuation * vec3(lights[index].specular) * vec3(material.specular) * pow(max(0.0, dot(reflect(-lightDirection, normalDirection), viewDirection)), material.shininess);
		}
		
		totalLighting = totalLighting + diffuseReflection + specularReflection;
	}
 
	vec4 textureColor = texture(texImage, varying_textureCoord);
	fragColor = vec4(totalLighting, 1.0) * textureColor;
}