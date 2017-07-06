module Jekyll
  class IndexPage < Page
    def initialize(site)
      @site = site
      @base = site.source
      @dir = '/'
      @name = 'index.html'

      self.process(@name)
    end

    def render(layouts, site_payload)
      versions = Dir.glob('v*').select {|f| File.directory? f}.collect {|v| Version.new(v) }
      
      currentVersion = compute_last_version(versions)

      self.output = %Q(
          <html>
            <head>
              <meta http-equiv="refresh" content="0; url=#{to_url(currentVersion)}" />
            </head>
            <body>
              <ul>
              )
      self.output += generate_list(versions)
      self.output += %Q(
              </ul>
            </body>
          </html>
        )
    end
    
    def compute_last_version(versions)
      sorted = versions.sort { |x,y| y <=> x }
      sorted[0]
    end
    
    def to_url(version)
      @site.config['url'] + '/' + version.name + '/'
    end
    
    def generate_list(versions)
      list = ""
      for version in versions
        list += %Q(
          <li><a href="#{to_url(version)}">#{version.name}</a></li>
        )
      end
      list
    end
  end
  
  
  class Version
    attr_accessor :name, :major, :minor, :patch, :snapshot
    
    def initialize name
      @name = name
      parts = name.split('.')
      @major = parts[0].gsub('v', '').to_i
      @minor = parts[1].to_i
      @patch = parts[2].gsub('-SNAPSHOT', '').to_i
      @snapshot = name.end_with?('-SNAPSHOT')
    end
    def <=> other
      if @major != other.major
        @major <=> other.major
      elsif @minor != other.minor
        @minor <=> other.minor
      elsif @patch != other.patch
        @patch <=> other.patch
      elsif @snapshot != other.snapshot
        if @snapshot
          -1
        else
          1
        end
      else
        0
      end
    end
    
    def to_s
      @name
    end
  end

  class IndexGenerator < Generator
    def generate(site)
      site.pages << IndexPage.new(site)
    end
  end

end